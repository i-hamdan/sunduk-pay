package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.bxb.sunduk_pay.util.TransactionType.CREDIT;

@Log4j2
@Component
public class TransactionMapperImpl implements TransactionMapper {
    private final Validations validations;

    public TransactionMapperImpl(Validations validations) {
        this.validations = validations;
    }

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transaction.getTransactionId());
        transactionResponse.setUuid(transaction.getUser().getUuid());
        transactionResponse.setTransactionType(transaction.getTransactionType());
        transactionResponse.setDescription(transaction.getDescription());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setMainWalletId(transaction.getMainWallet().getMainWalletId());
        transactionResponse.setFullName(transaction.getUser().getFullName());
        transactionResponse.setDateTime(transaction.getDateTime());
        transactionResponse.setTransactionLevel(transaction.getTransactionLevel());
        transactionResponse.setFromWallet(transaction.getFromWallet());
        transactionResponse.setFromWalletId(transaction.getFromWalletId());
        transactionResponse.setToWallet(transaction.getToWallet());
        transactionResponse.setToWalletId(transaction.getToWalletId());
        return transactionResponse;
    }

    @Override
    public List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions) {
        List<TransactionResponse> responses = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            responses.add(toTransactionResponse(transaction));
        }
        return responses;
    }

    public TransactionEvent toTransactionEvent(Transaction transaction) {
        TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setWalletId(transaction.getMainWallet().getMainWalletId());
        transactionEvent.setTransactionId(transaction.getTransactionId());
        transactionEvent.setTransactionType(transaction.getTransactionType());
        transactionEvent.setTransactionLevel(transaction.getTransactionLevel());
        transactionEvent.setFromWallet(transaction.getFromWallet());
        transactionEvent.setFromWalletId(transaction.getFromWalletId());
        transactionEvent.setToWallet(transaction.getToWallet());
        transactionEvent.setToWalletId(transaction.getToWalletId());
        transactionEvent.setAmount(transaction.getAmount());
        transactionEvent.setDateTime(transaction.getDateTime());
        transactionEvent.setEmail(transaction.getUser().getEmail());
        transactionEvent.setUuid(transaction.getUser().getUuid());
        transactionEvent.setFullName(transaction.getUser().getFullName());
        transactionEvent.setPhoneNumber(transaction.getUser().getPhoneNumber());

        Double balance = null;

        try {
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                if (transaction.getToWalletId() != null &&
                        transaction.getToWalletId().equals(transaction.getMainWallet().getMainWalletId())) {
                    balance = transaction.getMainWallet().getBalance();
                } else if (transaction.getToWalletId() != null) {
                    SubWallet subWallet = validations.findSubWalletIfExists(
                            transaction.getMainWallet(),
                            transaction.getToWalletId()
                    );
                    if (subWallet != null) balance = subWallet.getBalance();
                }

            } else if (transaction.getTransactionType() == TransactionType.DEBIT) {
                if (transaction.getFromWalletId() != null &&
                        transaction.getFromWalletId().equals(transaction.getMainWallet().getMainWalletId())) {
                    balance = transaction.getMainWallet().getBalance();
                } else if (transaction.getFromWalletId() != null) {
                    SubWallet subWallet = validations.findSubWalletIfExists(
                            transaction.getMainWallet(),
                            transaction.getFromWalletId()
                    );
                    if (subWallet != null) balance = subWallet.getBalance();
                }
            }
        } catch (Exception e) {
            // fallback if something goes wrong
            balance = transaction.getMainWallet().getBalance();
            log.warn("Balance resolution failed for txn={}, falling back to mainWallet balance", transaction.getTransactionId(), e);
        }

        transactionEvent.setRemainingBalance(balance);
        return transactionEvent;
    }


}



