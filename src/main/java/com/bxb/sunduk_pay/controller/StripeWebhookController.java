package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.service.WalletService;
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

    private final WalletService walletService;

    @Autowired
    public StripeWebhookController(WalletService walletService) {
        this.walletService = walletService;
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
                String purpose = session.getMetadata().get("purpose"); // Important
                double amount = session.getAmountTotal() / 100.0;
                String paymentIntentId = session.getPaymentIntent();

                if ("add-money".equals(purpose)) {
                    walletService.addMoneyToWallet(userId, amount, paymentIntentId);
                } else if ("wallet-pay".equals(purpose)) {
                    walletService.payMoneyFromWallet(userId, amount, "Paid via Stripe Checkout");
                }
            }
        }
    }
}