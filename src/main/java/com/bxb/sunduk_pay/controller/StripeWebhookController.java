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
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * Controller to handle Stripe webhook events such as completed payments,
 * failed payments, and expired checkout sessions.
 */
@Log4j2
@RestController
public class StripeWebhookController {

    private final WalletService walletService;
    private final FailedTxnRecorder failedTxnRecorder;
    public StripeWebhookController(WalletService walletService,
                                   FailedTxnRecorder failedTxnRecorder) {
        this.walletService = walletService;
        this.failedTxnRecorder = failedTxnRecorder;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param request the HttpServletRequest containing Stripe payload
     * @return a response indicating the processing result
     * @throws IOException if reading the request payload fails
     */
    @PostMapping("/webhook")
    public MainWalletResponse handleStripeEvent(HttpServletRequest request) throws IOException {
        String payload;
        String sigHeader = request.getHeader("Stripe-Signature");
        try {
            payload = IOUtils.toString(request.getInputStream(), "UTF-8");
        } catch (IOException e) {
            log.error("Failed to read Stripe webhook payload");
            throw new InvalidPayloadException("Invalid Stripe payload");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            log.error("Invalid Stripe signature.");
            throw new StripeSessionException("Invalid Stripe signature");
        }
        try {
            switch (event.getType()) {
                case "checkout.session.completed": {
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);

                    if (session != null) {
                        return handleCompletedSession(session);
                    }
                    break;
                }

                case "checkout.session.expired":
                case "checkout.session.async_payment_failed":
                case "payment_intent.payment_failed": {
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);

                    if (session != null) {
                        log.warn("Payment failed for sessionId={}", session.getId());

                        // Build failed transaction request
                        MainWalletRequest requestObj = new MainWalletRequest();
                        requestObj.setUuid(session.getMetadata().get("userId"));
                        requestObj.setAmount(session.getAmountTotal() / 100.0);
                        requestObj.setTransactionType(TransactionType.valueOf(session.getMetadata()
                                .get("type")));
                        requestObj.setSourceWalletId(session.getMetadata().get("sourceWallet"));
                        requestObj.setTargetWalletId(session.getMetadata().get("targetWallet"));
                        // Save transaction with FAILED status
                        return failedTxnRecorder.recordFailedTxn(requestObj);
                    }
                    break;
                }
                default:
                    log.info("Unhandled Stripe event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Unexpected error handling Stripe webhook. Event={}", event);
            throw new StripeSessionException("Failed to process Stripe webhook");
        }
        return MainWalletResponse.builder().message("Success").build();
    }

    /**
     * Handles a completed Stripe checkout session.
     *
     * @param session Stripe event object
     * @return response after processing payment
     */
    private MainWalletResponse handleCompletedSession(Session session) {
        String userId = session.getMetadata().get("userId");
        TransactionType transactionType = TransactionType.valueOf(
                session.getMetadata().get("type").toUpperCase()
        );
        String targetWallet = session.getMetadata().get("targetWallet");
        String sourceWallet = session.getMetadata().get("sourceWallet");
        double amount = session.getAmountTotal() / 100.0;

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