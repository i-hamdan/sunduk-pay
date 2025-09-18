package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of TransactionMapper to convert Transaction entities to DTOs and Kafka events.
 */
@Log4j2
@Component
public class TransactionMapperImpl implements TransactionMapper {

    private final Validations validations;

    public TransactionMapperImpl(Validations validations) {
        this.validations = validations;
    }

    @Override
    public TransactionResponse toTransactionResponse(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                .withLocale(Locale.ENGLISH);

        TransactionResponse response = TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .groupId(transaction.getGroupId())
                .uuid(transaction.getUser().getUuid())
                .transactionType(transaction.getTransactionType())
                .paymentMethod(transaction.getPaymentMethod())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .mainWalletId(transaction.getMainWallet().getMainWalletId())
                .status(transaction.getStatus())
                .fullName(transaction.getUser().getFullName())
                .date(transaction.getDateTime().format(formatter))
                .transactionLevel(transaction.getTransactionLevel())
                .fromWallet(transaction.getFromWallet())
                .fromWalletId(transaction.getFromWalletId())
                .fromWalletIcon(validations.getFromIconOfTxn(transaction.getMainWallet().getMainWalletId(), transaction.getFromWalletId()))
                .toWallet(transaction.getToWallet())
                .toWalletId(transaction.getToWalletId())
                .toWalletIcon(validations.getToIconOfTxn(transaction.getMainWallet().getMainWalletId(), transaction.getToWalletId()))
                .build();

        return response;
    }

    @Override
    public List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions) {
        List<TransactionResponse> responses = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            responses.add(toTransactionResponse(transaction));
        }
        return responses;
    }

    @Override
    public TransactionEvent toTransactionEvent(Transaction transaction) {
        TransactionEvent event = TransactionEvent.builder()
                .walletId(transaction.getMainWallet().getMainWalletId())
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .transactionLevel(transaction.getTransactionLevel())
                .fromWallet(transaction.getFromWallet())
                .fromWalletId(transaction.getFromWalletId())
                .toWallet(transaction.getToWallet())
                .toWalletId(transaction.getToWalletId())
                .amount(transaction.getAmount())
                .dateTime(transaction.getDateTime())
                .email(transaction.getUser().getEmail())
                .uuid(transaction.getUser().getUuid())
                .fullName(transaction.getUser().getFullName())
                .phoneNumber(transaction.getUser().getPhoneNumber())
                .remainingBalance(resolveBalance(transaction))
                .build();

        return event;
    }

    /**
     * Resolves the remaining balance for the transaction depending on type and wallet.
     */
    private Double resolveBalance(Transaction transaction) {
        try {
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                return resolveCreditBalance(transaction);
            } else if (transaction.getTransactionType() == TransactionType.DEBIT) {
                return resolveDebitBalance(transaction);
            }
        } catch (Exception e) {
            log.warn("Balance resolution failed for txn={}, falling back to mainWallet balance",
                    transaction.getTransactionId(), e);
        }
        return transaction.getMainWallet().getBalance();
    }

    private Double resolveCreditBalance(Transaction transaction) {
        if (transaction.getToWalletId() != null) {
            if (transaction.getToWalletId().equals(transaction.getMainWallet().getMainWalletId())) {
                return transaction.getMainWallet().getBalance();
            }
            SubWallet subWallet = validations.findSubWalletIfExists(transaction.getMainWallet(), transaction.getToWalletId());
            if (subWallet != null) return subWallet.getBalance();
        }
        return transaction.getMainWallet().getBalance();
    }

    private Double resolveDebitBalance(Transaction transaction) {
        if (transaction.getFromWalletId() != null) {
            if (transaction.getFromWalletId().equals(transaction.getMainWallet().getMainWalletId())) {
                return transaction.getMainWallet().getBalance();
            }
            SubWallet subWallet = validations.findSubWalletIfExists(transaction.getMainWallet(), transaction.getFromWalletId());
            if (subWallet != null) return subWallet.getBalance();
        }
        return transaction.getMainWallet().getBalance();
    }
}
