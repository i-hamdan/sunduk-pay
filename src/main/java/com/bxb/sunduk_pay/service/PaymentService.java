package com.bxb.sunduk_pay.service;

import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {

    CompletableFuture<ResponseEntity<String>> createCheckoutSession(String userId, Double amount, String type);
}
