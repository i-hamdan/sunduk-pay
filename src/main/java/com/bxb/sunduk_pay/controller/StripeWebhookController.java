package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.exception.StripeSessionException;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controller to handle Stripe webhook events such as completed payments,
 * failed payments, and expired checkout sessions.
 */
@Log4j2
@RestController
public class StripeWebhookController {

    /** Service for wallet operations. */
    private final WalletService walletService;

    /** Service to record failed transactions. */
    private final FailedTxnRecorder failedTxnRecorder;

    /** Stripe webhook endpoint secret. */
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    /** Divisor to convert amount from cents to major units. */
    private static final double AMOUNT_DIVISOR = 100.0;

    /**
     * Constructor-based dependency injection.
     *
     * @param walletServiceParam      service for wallet operations
     * @param failedTxnRecorderParam  service to record failed transactions
     */
    public StripeWebhookController(final WalletService walletServiceParam,
                                   final FailedTxnRecorder failedTxnRecorderParam) {
        this.walletService = walletServiceParam;
        this.failedTxnRecorder = failedTxnRecorderParam;
    }

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param request the HttpServletRequest containing Stripe payload
     * @return a response indicating the processing result
     * @throws IOException if reading the request payload fails
     */
    @PostMapping("/webhook")
    public MainWalletResponse handleStripeEvent(final HttpServletRequest request)
            throws IOException {

        final String sigHeader = request.getHeader("Stripe-Signature");
        final String payload;

        try {
            payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read Stripe webhook payload", e);
            throw new InvalidPayloadException("Invalid Stripe payload");
        }

        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature.", e);
            throw new StripeSessionException("Invalid Stripe signature");
        } catch (Exception e) {
            log.error("Unexpected error verifying Stripe webhook.", e);
            throw new StripeSessionException("Failed to verify Stripe webhook");
        }

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    return handleCompletedSession(event);
                case "checkout.session.expired":
                case "checkout.session.async_payment_failed":
                case "payment_intent.payment_failed":
                    return handleFailedSession(event);
                default:
                    log.info("Unhandled Stripe event type: {}", event.getType());
                    return MainWalletResponse.builder()
                            .message("Success")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error handling Stripe webhook event: {}", event, e);
            throw new StripeSessionException("Failed to process Stripe webhook");
        }
    }

    /**
     * Handles a completed Stripe checkout session.
     *
     * @param event Stripe event object
     * @return response after processing payment
     */
    private MainWalletResponse handleCompletedSession(final Event event) {
        final Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.warn("Session object is null for completed checkout event.");
            return MainWalletResponse.builder()
                    .message("No session data")
                    .build();
        }

        final String userId = session.getMetadata().get("userId");
        final TransactionType transactionType = TransactionType.valueOf(
                session.getMetadata().get("type").toUpperCase()
        );
        final String sourceWallet = session.getMetadata().get("sourceWallet");
        final String targetWallet = session.getMetadata().get("targetWallet");
        final double amount = session.getAmountTotal() / AMOUNT_DIVISOR;

        final MainWalletRequest requestObj = new MainWalletRequest();
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

    /**
     * Handles failed or expired Stripe sessions and records them.
     *
     * @param event Stripe event object
     * @return response after recording failed transaction
     */
    private MainWalletResponse handleFailedSession(final Event event) {
        final Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.warn("Session object is null for failed/expired checkout event.");
            return MainWalletResponse.builder()
                    .message("No session data")
                    .build();
        }

        log.warn("Payment failed for sessionId={}", session.getId());

        final MainWalletRequest requestObj = new MainWalletRequest();
        requestObj.setUuid(session.getMetadata().get("userId"));
        requestObj.setAmount(session.getAmountTotal() / AMOUNT_DIVISOR);
        requestObj.setTransactionType(
                TransactionType.valueOf(session.getMetadata().get("type"))
        );
        requestObj.setSourceWalletId(session.getMetadata().get("sourceWallet"));
        requestObj.setTargetWalletId(session.getMetadata().get("targetWallet"));

        return failedTxnRecorder.recordFailedTxn(requestObj);
    }
}
