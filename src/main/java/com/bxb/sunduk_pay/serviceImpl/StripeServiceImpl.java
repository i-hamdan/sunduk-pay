package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
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
    public Session createCheckoutSession(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet) throws Exception {
        String productName;
        String successUrl;
        String cancelUrl;

        switch (transactionType.toString()) {
            case "CREDIT":
                productName = "Add Money to Wallet";
                successUrl = "http://localhost:5173/add-success";
                cancelUrl = "http://localhost:5173/add-cancel";
                break;

            case "DEBIT":
                productName = "Pay From Wallet";
                successUrl = "http://localhost:5173/pay-success";
                cancelUrl = "http://localhost:5173/pay-cancel";
                break;

            default:
                throw new IllegalArgumentException("Invalid session type: " + transactionType + ". Allowed: ADD, PAY");
        }

        return createSession(userId, amount, productName, transactionType, successUrl, cancelUrl,sourceWallet,targetWallet);
    }

    // Reusable method for creating a Stripe checkout session
    private Session createSession(String userId, Double amount, String productName, TransactionType transactionType,
                                  String successUrl, String cancelUrl, WalletWrapper sourceWallet, WalletWrapper targetWallet) throws Exception {
        long amountInCents = (long) (amount * 100);

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

        return Session.create(params);
    }

}