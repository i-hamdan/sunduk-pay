package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.exception.StripeSessionException;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;


/**
 * Controller to handle Stripe webhook events such as completed payments,
 * failed payments, and expired checkout sessions.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
public class StripeWebhookController {

    /** Constant to convert cents to dollars. */
    private static final double VALUE = 100.0;
    /** Services for wallet operations and recording failed transactions. */
    private final WalletService walletService;

    /** Service to record failed transactions. */
    private final FailedTxnRecorder failedTxnRecorder;


    /** Stripe webhook endpoint secret for signature verification.*/
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param request the HttpServletRequest
     *                containing Stripe payload
     * @return a response indicating the
     * processing result
     * @throws IOException if reading the
     * request payload fails
     */
    @PostMapping("/webhook")
    public MainWalletResponse handleStripeEvent(
         final HttpServletRequest request) throws IOException {
        // --- read payload ---
        String payload;
        String sigHeader = request.getHeader("Stripe-Signature");
        try {
            payload = IOUtils.toString(request.getInputStream(),
                    "UTF-8");
        } catch (IOException e) {
            log.error("Failed to read Stripe webhook payload");
            throw new InvalidPayloadException("Invalid Stripe payload");
        }

        // --- construct event ---
        Event event;
        try {
            event = Webhook.constructEvent(payload,
                    sigHeader,
                    endpointSecret);
        } catch (Exception e) {
            log.error("Invalid Stripe signature.");
            throw new StripeSessionException("Invalid Stripe signature");
        }

        // --- handle event ---
        try {
            Session session = null; // one variable reused
            switch (event.getType()) {
                case "checkout.session.completed":
                    session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);
                    if (session != null) {
                        return handleCompletedSession(session);
                    }
                    break;

                case "checkout.session.expired":
                case "checkout.session.async_payment_failed":
                case "payment_intent.payment_failed":
                    session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);
                    if (session != null) {
                        log.warn(
                                "Payment failed for sessionId={}",
                                session.getId());

                        MainWalletRequest requestObj = new MainWalletRequest();
                        requestObj.setUuid(Long.parseLong(session.getMetadata().get("userId")));
                        requestObj.setAmount(session.getAmountTotal() / VALUE);
                        requestObj.setTransactionType(TransactionType.valueOf(
                                session.getMetadata().get("type")));
                        requestObj.setSourceWalletId(Long.parseLong(session.getMetadata()
                                .get("sourceWallet")));
                        requestObj.setTargetWalletId(Long.parseLong(session.getMetadata()
                                .get("targetWallet")));
                        return failedTxnRecorder.recordFailedTxn(requestObj);
                    }
                    break;

                default:
                    log.info(
                       "Unhandled Stripe event type: {}",
                            event.getType());
            }
        } catch (Exception e) {
            log.error(
          "Unexpected error handling Stripe webhook. Event={}",
                    event);
            throw new StripeSessionException(
                    "Failed to process Stripe webhook");
        }

        return MainWalletResponse.builder()
                .message("Success").build();
    }


    /**
     * Handles a completed Stripe checkout session.
     *
     * @param session Stripe event object
     * @return response after processing payment
     */
    private MainWalletResponse handleCompletedSession(
            final Session session) {
        Long userId = Long.parseLong(session.getMetadata().get("userId"));
        TransactionType transactionType = TransactionType.valueOf(
                session.getMetadata().get("type").toUpperCase()
        );
        Long targetWallet = Long.parseLong(session.getMetadata().get("targetWallet"));
        Long sourceWallet = Long.parseLong(session.getMetadata().get("sourceWallet"));
        double amount = session.getAmountTotal() / VALUE;

        MainWalletRequest requestObj = new MainWalletRequest();
        requestObj.setUuid(userId);
        requestObj.setAmount(amount);
        requestObj.setTransactionType(transactionType);
        requestObj.setSourceWalletId(sourceWallet);
        requestObj.setTargetWalletId(targetWallet);

        if (transactionType.equals(TransactionType.DEBIT)) {
            return walletService.payMoney(requestObj);
        } else {
            return walletService.addMoney(requestObj);
        }
    }
}
