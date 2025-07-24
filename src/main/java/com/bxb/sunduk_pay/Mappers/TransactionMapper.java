package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.request.TransactionRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;

public interface TransactionMapper {
    TransactionResponse toTransactionResponse(Transaction transaction);
    Transaction toTransaction(TransactionRequest request);
}
