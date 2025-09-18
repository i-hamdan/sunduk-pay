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
 * Implementation of TransactionMapper to convert Transaction entities
 * to DTOs and Kafka events.
 */
@Log4j2
@Component
public  class TransactionMapperImpl implements TransactionMapper {

    /** Validation utility for transaction-related checks. */
    private final Validations validations;

    /**
     * Constructor with validations dependency.
     *
     * @param validations the validation utility
     */
    public TransactionMapperImpl(final Validations validations) {
        this.validations = validations;
    }

    /**
     * Converts a Transaction entity into a TransactionResponse DTO.
     *
     * @param transaction the transaction entity
     * @return the corresponding TransactionResponse DTO
     */
    @Override
    public TransactionResponse toTransactionResponse(final Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd MMMM yyyy")
                .withLocale(Locale.ENGLISH);

        return TransactionResponse.builder()
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
                .fromWalletIcon(
                        validations.getFromIconOfTxn(
                                transaction.getMainWallet().getMainWalletId(),
                                transaction.getFromWalletId()
                        )
                )
                .toWallet(transaction.getToWallet())
                .toWalletId(transaction.getToWalletId())
                .toWalletIcon(
                        validations.getToIconOfTxn(
                                transaction.getMainWallet().getMainWalletId(),
                                transaction.getToWalletId()
                        )
                )
                .build();
    }

    /**
     * Converts a list of Transaction entities into a list of
     * TransactionResponse DTOs.
     *
     * @param transactions the list of transactions
     * @return the list of TransactionResponse DTOs
     */
    @Override
    public List<TransactionResponse> toTransactionsResponse(
            final List<Transaction> transactions) {
        List<TransactionResponse> responses = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            responses.add(toTransactionResponse(transaction));
        }
        return responses;
    }

    /**
     * Converts a Transaction entity into a TransactionEvent for Kafka.
     *
     * @param transaction the transaction entity
     * @return the TransactionEvent object
     */
    @Override
    public TransactionEvent toTransactionEvent(final Transaction transaction) {
        return TransactionEvent.builder()
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
    }

    /**
     * Resolves the remaining balance for the transaction
     * depending on type and wallet.
     *
     * @param transaction the transaction entity
     * @return the resolved balance
     */
    private Double resolveBalance(final Transaction transaction) {
        try {
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                return resolveCreditBalance(transaction);
            } else if (transaction.getTransactionType() == TransactionType.DEBIT) {
                return resolveDebitBalance(transaction);
            }
        } catch (Exception e) {
            log.warn(
                    "Balance resolution failed for txn={}, falling back to mainWallet balance",
                    transaction.getTransactionId(),
                    e
            );
        }
        return transaction.getMainWallet().getBalance();
    }

    /**
     * Resolves credit balance for the given transaction.
     *
     * @param transaction the transaction entity
     * @return the balance after credit
     */
    private Double resolveCreditBalance(final Transaction transaction) {
        if (transaction.getToWalletId() != null) {
            if (transaction.getToWalletId()
                    .equals(transaction.getMainWallet().getMainWalletId())) {
                return transaction.getMainWallet().getBalance();
            }
            SubWallet subWallet = validations.findSubWalletIfExists(
                    transaction.getMainWallet(), transaction.getToWalletId());
            if (subWallet != null) {
                return subWallet.getBalance();
            }
        }
        return transaction.getMainWallet().getBalance();
    }

    /**
     * Resolves debit balance for the given transaction.
     *
     * @param transaction the transaction entity
     * @return the balance after debit
     */
    private Double resolveDebitBalance(final Transaction transaction) {
        if (transaction.getFromWalletId() != null) {
            if (transaction.getFromWalletId()
                    .equals(transaction.getMainWallet().getMainWalletId())) {
                return transaction.getMainWallet().getBalance();
            }
            SubWallet subWallet = validations.findSubWalletIfExists(
                    transaction.getMainWallet(), transaction.getFromWalletId());
            if (subWallet != null) {
                return subWallet.getBalance();
            }
        }
        return transaction.getMainWallet().getBalance();
    }
}
