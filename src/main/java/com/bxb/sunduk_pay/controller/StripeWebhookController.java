package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factoryPattern.TransferService;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@RestController
public class StripeWebhookController {

    private final TransferService transferService;

    @Autowired
    public StripeWebhookController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public void handleStripeEvent(HttpServletRequest request) throws IOException, SignatureVerificationException {
        String payload = IOUtils.toString(request.getInputStream(), "UTF-8");
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Object rawData = event.getData().getObject();

            if (rawData instanceof Session session) {
                String userId = session.getMetadata().get("userId");
                TransactionType transactionType = TransactionType.valueOf(
                        session.getMetadata().get("type").toUpperCase()
                );
                double amount = session.getAmountTotal() / 100.0;
//                String paymentIntentId = session.getPaymentIntent();

                MainWalletRequest requestObj = new MainWalletRequest();
                requestObj.setUuid(userId);
                requestObj.setAmount(amount);
                requestObj.setTransactionType(transactionType);

                // Use the same service for both CREDIT and DEBIT
                transferService.perform(requestObj);
            }
        }
    }
}