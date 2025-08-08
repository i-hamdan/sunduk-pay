package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.event.TransactionEvent;
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
        transactionResponse.setWalletId(transaction.getMasterWalletId().getMainWalletId());
        transactionResponse.setFullName(transaction.getMasterWalletId().getUser().getFullName());
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
    public TransactionEvent toTransactionEvent(Transaction transaction){
        TransactionEvent transactionEvent =new TransactionEvent();
        transactionEvent.setWalletId(transaction.getMasterWalletId().getMainWalletId());
        transactionEvent.setTransactionId(transaction.getTransactionId());
        transactionEvent.setTransactionType(transaction.getTransactionType());
        transactionEvent.setAmount(transaction.getAmount());
        transactionEvent.setDateTime(transaction.getDateTime());
        transactionEvent.setEmail(transaction.getUser().getEmail());
        transactionEvent.setUuid(transaction.getUser().getUuid());
        transactionEvent.setFullName(transaction.getUser().getFullName());
        transactionEvent.setRemainingAmount(transaction.getMasterWalletId().getBalance());
        transactionEvent.setPhoneNumber(transaction.getUser().getPhoneNumber());
        return transactionEvent;
    }

    }



