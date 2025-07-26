package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Log4j2
@Service
public class StripeServiceImpl implements StripeService {

    @Autowired
    public StripeServiceImpl(@Value("${stripe.key.secret}") String secretKey) {
        Stripe.apiKey = secretKey;
        log.info("Stripe API key initialized.");
    }
    @Override
    public Session createCheckoutSession(String userId, Double amount) throws Exception {
        log.info("Creating Stripe Checkout session for userId: {}, amount: {}", userId, amount);
        try {
            Session session = createSession(userId, amount, "Add Money to Wallet", "add-money",
                    "http://localhost:5173/success", "http://localhost:5173/cancel");
            log.info("Stripe Checkout session created successfully for userId: {}", userId);
            return session;
        } catch (Exception e) {
            log.error("Failed to create Stripe Checkout session for userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Session createPaymentSession(String userId, Double amount) throws Exception {
        log.info("Creating Stripe Payment session for userId: {}, amount: {}", userId, amount);
try {
    Session session =  createSession(userId, amount, "Pay Money From Wallet", "wallet-pay",
            "http://localhost:5173/payment-success", "http://localhost:5173/payment-cancel");
    log.info("Stripe Payment session created successfully for userId: {}", userId);
    return session;
}
catch (Exception e) {
    log.error("Failed to create Stripe Payment session for userId: {}, error: {}", userId, e.getMessage(), e);
    throw new ResourceNotFoundException("Failed to create Stripe Payment session");
}

    }

    // Common reusable method
    private Session createSession(String userId, Double amount, String productName, String purpose, String successUrl, String cancelUrl) throws Exception {
        long amountInCents = (long) (amount * 100);

        log.info("Creating Stripe session | userId: {}, amount: {}, product: {}, purpose: {}", userId, amount, productName, purpose);
        log.debug("Success URL: {}, Cancel URL: {}", successUrl, cancelUrl);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("purpose", purpose);

        try {
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

            Session session = Session.create(params);
            log.info("Stripe session successfully created for userId: {} with sessionId: {}", userId, session.getId());
            return session;

        } catch (Exception e) {
            log.error("Failed to create Stripe session for userId: {}, reason: {}", userId, e.getMessage(), e);
            throw new ResourceNotFoundException("Failed to create Stripe session");
        }
    }

}