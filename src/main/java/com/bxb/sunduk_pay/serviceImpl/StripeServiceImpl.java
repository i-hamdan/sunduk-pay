package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exceptions.StripeSessionException;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling Stripe checkout sessions.
 */
@Log4j2
@Service
public class StripeServiceImpl implements StripeService {

    public StripeServiceImpl(@Value("${stripe.key.secret}") String secretKey) {
        Stripe.apiKey = secretKey;
        log.info("Stripe API key initialized.");
    }

    /**
     * Creates a Stripe checkout session for a user.
     *
     * @param userId          the user ID
     * @param amount          the transaction amount
     * @param transactionType CREDIT or DEBIT
     * @param targetWallet    the target wallet
     * @param sourceWallet    the source wallet
     * @return Stripe Session object
     * @throws StripeSessionException if session creation fails
     */
    @Override
    public Session createCheckoutSession(String userId, Double amount,
                                         TransactionType transactionType,
                                         WalletWrapper targetWallet,
                                         WalletWrapper sourceWallet) throws StripeSessionException {
        log.info("Creating Stripe checkout session for userId={}, amount={}, type={}", userId, amount, transactionType);

        String productName;
        String successUrl = "islamicbank://payment-success";
        String cancelUrl = "islamicbank://payment-failed";

        switch (transactionType) {
            case CREDIT -> productName = "Add Money to Wallet";
            case DEBIT -> productName = "Pay From Wallet";
            default -> {
                log.error("Invalid transaction type: {}", transactionType);
                throw new StripeSessionException("Invalid session type: " + transactionType);
            }
        }

        try {
            return createSession(userId, amount, productName, transactionType, successUrl, cancelUrl, sourceWallet, targetWallet);
        } catch (StripeException e) {
            log.error("Stripe API error: userId={}, amount={}, type={}, error={}", userId, amount, transactionType, e.getMessage(), e);
            throw new StripeSessionException("Stripe session creation failed. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error creating Stripe session: userId={}, amount={}, type={}, error={}", userId, amount, transactionType, e.getMessage(), e);
            throw new StripeSessionException("Unexpected error during Stripe session creation.");
        }
    }

    /**
     * Internal method to build and create a Stripe session.
     */
    private Session createSession(String userId, Double amount, String productName,
                                  TransactionType transactionType,
                                  String successUrl, String cancelUrl,
                                  WalletWrapper sourceWallet, WalletWrapper targetWallet) throws StripeException {

        long amountInCents = (long) (amount * 100);
        log.debug("Preparing Stripe session: productName={}, amountInCents={}, userId={}", productName, amountInCents, userId);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("type", transactionType.toString());
        metadata.put("amount", amount.toString());
        metadata.put("sourceWallet", sourceWallet != null ? sourceWallet.getId() : null);
        metadata.put("targetWallet", targetWallet != null ? targetWallet.getId() : null);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amountInCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(productName)
                                        .build())
                                .build())
                        .build())
                .putAllMetadata(metadata)
                .build();

        log.debug("Stripe session params built successfully for userId={}", userId);
        return Session.create(params);
    }
}
