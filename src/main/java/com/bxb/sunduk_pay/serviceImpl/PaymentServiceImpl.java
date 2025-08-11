package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.stripe.model.checkout.Session;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PaymentServiceImpl implements PaymentService {

   private final StripeService stripeService;

    public PaymentServiceImpl(StripeService stripeService) {
        this.stripeService = stripeService;
    }


    @Override
    @CircuitBreaker(name = "stripeGateway", fallbackMethod = "paymentFallback")
    public CompletableFuture<ResponseEntity<String>> createCheckoutSession(String userId, Double amount, TransactionType transactionType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Session session = stripeService.createCheckoutSession(userId, amount, transactionType);
                return ResponseEntity.ok(session.getUrl());

            } catch (Exception e) {
                throw new RuntimeException("Stripe session creation failed", e);
            }
        });
    }

    public CompletableFuture<ResponseEntity<String>> paymentFallback(String userId, Double amount,String purpose, Throwable t) {
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Payment provider unavailable: " + t.getMessage())
        );
    }


}


