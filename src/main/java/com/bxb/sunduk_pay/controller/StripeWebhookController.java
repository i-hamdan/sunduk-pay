package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.exception.StripeSessionException;
import com.bxb.sunduk_pay.factoryPattern.TransferService;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
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

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Log4j2
@RestController
public class StripeWebhookController {

    private final WalletService walletService;

    public StripeWebhookController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;


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
            if ("checkout.session.completed".equals(event.getType())) {
                Object rawData = event.getData().getObject();

                if (rawData instanceof Session session) {
                    String userId = session.getMetadata().get("userId");
                    TransactionType transactionType = TransactionType.valueOf(
                            session.getMetadata().get("type").toUpperCase()
                    );
                    String targetWallet = session.getMetadata().get("targetWallet");
                    String sourceWallet = session.getMetadata().get("sourceWallet");
                    double amount = session.getAmountTotal() / 100.0;
//                String paymentIntentId = session.getPaymentIntent();

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
        } catch (Exception e) {
            log.error(" Unexpected error handling Stripe webhook. Event={}", event);
            throw new StripeSessionException("Failed to process Stripe webhook");
        }
        return MainWalletResponse.builder().message("Success").build();
    }
}