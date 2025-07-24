package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.request.TransactionRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import org.springframework.stereotype.Component;


@Component
public class TransactionMapperImpl implements TransactionMapper {
   public TransactionResponse toTransactionResponse(Transaction transaction){
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transaction.getTransactionId());
        transactionResponse.setTransactionType(transaction.getTransactionType());
        transactionResponse.setDescription(transaction.getDescription());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setDateTime(transaction.getDateTime());
        transactionResponse.setWalletId(transaction.getWallet().getWalletId());
        transactionResponse.setFullName(transaction.getWallet().getUser().getFullName());
        return transactionResponse;
    }

    public Transaction toTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(request.getTransactionType());
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        return transaction;
    }

}
