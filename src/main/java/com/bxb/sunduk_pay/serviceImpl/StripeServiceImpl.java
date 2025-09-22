package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.StripeSessionException;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
    /**
     * Initializes the Stripe API with the secret key from application properties.
     *
     * @param secretKey the Stripe secret key
     */
    @Autowired
    public StripeServiceImpl(@Value("${stripe.key.secret}") final String secretKey) {
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
    public Session createCheckoutSession(final String userId ,
                                         final Double amount ,
                                         final TransactionType transactionType ,
                                         final WalletWrapper targetWallet ,
                                         final WalletWrapper sourceWallet) throws Exception {
        log.info(
                "Creating Stripe checkout session for userId={}, amount={}, type={}"
                , userId, amount, transactionType);

        String productName;
        String successUrl;
        String cancelUrl;

        switch (transactionType.toString()) {
            case "CREDIT":
                productName = "Add Money to Wallet";
                successUrl = "islamicbank://payment-success";
                cancelUrl = "islamicbank://payment-failed";
                break;

            case "DEBIT":
                productName = "Pay From Wallet";
                successUrl = "islamicbank://payment-success";
                cancelUrl = "islamicbank://payment-failed";
                break;

            default:
                log.error("Invalid transaction type: {}" ,
                        transactionType);
                throw new StripeSessionException("Invalid session type: " + transactionType);        }
try {
        return createSession(userId ,
                amount ,
                productName ,
                transactionType ,
                successUrl ,
                cancelUrl ,
                sourceWallet ,
                targetWallet);
    }catch (StripeException e) {
    log.error(
            "Stripe API error while creating checkout session. userId={}, amount={}, type={}, error={}",
            userId, amount, transactionType, e.getMessage());
    throw new StripeSessionException("Stripe session creation failed. Please try again later.");
} catch (Exception e) {
    log.error(
            "Unexpected error while creating Stripe session. userId={}, amount={}, type={}, error={}",
            userId, amount, transactionType, e.getMessage());
    throw new StripeSessionException("Unexpected error during Stripe session creation.");
}
    }

    /**
     * Internal method to build and create a Stripe session.
     */
    private Session createSession(final String userId ,
                                  final Double amount ,
                                  final String productName ,
                                  final TransactionType transactionType ,
                                  final String successUrl ,
                                  final String cancelUrl ,
                                  final WalletWrapper sourceWallet ,
                                  final WalletWrapper targetWallet) throws StripeSessionException , StripeException {
        long amountInCents = (long) (amount * 100);
        log.debug(
                "Creating Stripe session: productName={}, amountInCents={}, userId={}",
                productName, amountInCents, userId);


        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("type", transactionType.toString());
        metadata.put("amount",amount.toString());
        if (sourceWallet != null) {
            metadata.put("sourceWallet", sourceWallet.getId());
        } else {
            metadata.put("sourceWallet", null);
        }
        if (targetWallet != null) {
            metadata.put("targetWallet",targetWallet.getId());
        } else {
            metadata.put("targetWallet", null);
        }



        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(productName)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putAllMetadata(metadata)
                .build();

        log.debug("Stripe session params built successfully for userId={}", userId);

        return Session.create(params);
    }

}