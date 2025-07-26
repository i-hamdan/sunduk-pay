package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;

import java.util.List;

public interface TransactionMapper {
    TransactionResponse toTransactionResponse(Transaction transaction);
    List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions);
}
