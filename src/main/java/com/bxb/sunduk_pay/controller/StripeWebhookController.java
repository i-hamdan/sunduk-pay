package com.bxb.sunduk_pay.controller;

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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@RestController
public class StripeWebhookController {

    private final WalletService walletService;

    public StripeWebhookController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;



    @PostMapping("/webhook")
    public MainWalletResponse handleStripeEvent(HttpServletRequest request) throws IOException, SignatureVerificationException {
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

                if (transactionType.equals(TransactionType.DEBIT)) {
                    return walletService.payMoney(requestObj);
                }
                else {
                    return walletService.addMoney(requestObj);
                }
            }
        }
        return null;
    }
}