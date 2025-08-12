package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {

    MainWalletResponse createCheckoutSession(String userId, Double amount, TransactionType transactionType);
}
