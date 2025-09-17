package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.TransactionRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;

public interface TransactionService {
    String createTransaction(TransactionRequest request);

    TransactionResponse getInfoById(String id);
}
