package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class TransactionMapperImpl implements TransactionMapper {
   public TransactionResponse toTransactionResponse(Transaction transaction){
        TransactionResponse transactionResponse = new TransactionResponse();
transactionResponse.setUuid(transaction.getUser().getUuid());
        transactionResponse.setTransactionType(transaction.getTransactionType());
        transactionResponse.setDescription(transaction.getDescription());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setDateTime(transaction.getTimestamp());
        transactionResponse.setWalletId(transaction.getWallet().getWalletId());
        transactionResponse.setFullName(transaction.getWallet().getUser().getFullName());
        return transactionResponse;
    }

    @Override
    public List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions) {
            List<TransactionResponse> responses = new ArrayList<>(transactions.size());
            for(Transaction transaction : transactions){
                responses.add(toTransactionResponse(transaction));
            }
            return responses;
        }

    }



