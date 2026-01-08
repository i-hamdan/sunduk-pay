package com.bxb.sunduk_pay.Mappers;

//import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
//import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;
//import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.util.EmailCategory;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of TransactionMapper to convert Transaction
 * entities to DTOs and Kafka events.
 * to DTOs and Kafka events.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TransactionMapperImpl implements TransactionMapper {
    /** Validations utility for input validation and data retrieval. **/
    private final Validations validations;

    /** Date formatter for "dd MMMM yyyy" pattern in English locale. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy")
                    .withLocale(Locale.ENGLISH);
    /** DateTime formatter for "dd MMMM yyyy hh:mm a"
     *  pattern in English locale. */
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm a")
                    .withLocale(Locale.ENGLISH);


    /** {@inheritDoc} */
    public TransactionResponse toTransactionResponse(
            final Transaction transaction) {
//        if (transaction.getIsAnonymous()!=null && transaction.getIsAnonymous()){
//            return toAnonymousTransactionResponse(transaction);
//        }
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(
                transaction.getTransactionId());
        transactionResponse.setUuid(
                transaction.getUser().getUuid());
        transactionResponse.setFullName(transaction.getUser().getFullName());
        transactionResponse.setTransactionType(
                transaction.getTransactionType());
        transactionResponse.setPaymentMethod(
                transaction.getPaymentMethod());
        transactionResponse.setDescription(
                transaction.getDescription());
        transactionResponse.setAmount(
                transaction.getAmount());
        transactionResponse.setStatus(
                transaction.getStatus());
        transactionResponse.setDate(
                transaction.getDateTime().format(DATE_FORMATTER));
        transactionResponse.setDateTime(
                transaction.getDateTime().format(DATETIME_FORMATTER));
        transactionResponse.setChatDateTime(transaction.getDateTime());
        transactionResponse.setTransactionLevel(
                transaction.getTransactionLevel());
        if(transaction.getPaymentTag() != null){
            transactionResponse.setPaymentTag(transaction
                    .getPaymentTag());
        }
        transactionResponse.setFromWallet(
                transaction.getFromWallet());
        transactionResponse.setFromWalletId(
                transaction.getFromWalletId());
        transactionResponse.setFromPhoneNumber(
                transaction.getFromPhoneNumber());
        if (transaction.getIsInvestment()
                && transaction.getTransactionLevel() == TransactionLevel.INVESTED
                && transaction.getTransactionType()==TransactionType.CREDIT){
            transactionResponse.setFromWalletIcon("Investment");
        } else {
            transactionResponse.setFromWalletIcon(validations.getFromIconOfTxn(
                    transaction.getUser().getMainWallet().getMainWalletId(),
                    transaction.getFromWalletId()));
        }
        transactionResponse.setToWallet(transaction.getToWallet());
        transactionResponse.setToWalletId(transaction.getToWalletId());
        transactionResponse.setToPhoneNumber(transaction.getToPhoneNumber());
        if (transaction.getIsInvestment()
                && transaction.getTransactionLevel() == TransactionLevel.INVESTED
                && transaction.getTransactionType()==TransactionType.DEBIT){
            transactionResponse.setToWalletIcon("Investment");
        } else {
            transactionResponse.setToWalletIcon(validations.
                    getToIconOfTxn(transaction.getUser()
                                    .getMainWallet().getMainWalletId(),
                            transaction.getToWalletId()));
        }
        transactionResponse.setRecipientUpiId(
                transaction.getRecipientUpiId());
        if (transaction.getRiskLevel()!=null){
            transactionResponse.setRiskLevel(transaction.getRiskLevel().toString());
        }
        transactionResponse.setFromGlobalPotId(transaction.getFromGlobalPotId());
        transactionResponse.setToGlobalPotId(transaction.getToGlobalPotId());
        transactionResponse.setIsAnonymous(transaction.getIsAnonymous());
        transactionResponse.setAnonymousId(transactionResponse.getAnonymousId());
        transactionResponse.setAnonymousColor(transaction.getAnonymousColor());
        return transactionResponse;
    }

//    private TransactionResponse toAnonymousTransactionResponse(
//            Transaction transaction) {
//        TransactionResponse response = new TransactionResponse();
//
//        response.setTransactionId(transaction.getTransactionId());
//        response.setUuid(transaction.getUser().getUuid());
//
//        // Masked identity
//        response.setIsAnonymous(true);
//        response.setAnonymousId(transaction.getAnonymousId());
//        response.setAnonymousColor(transaction.getAnonymousColor());
//        response.setFullName("Anonymous user");
//
//        // Common fields
//        response.setTransactionType(transaction.getTransactionType());
//        response.setTransactionLevel(transaction.getTransactionLevel());
//        response.setPaymentMethod(transaction.getPaymentMethod());
//        response.setAmount(transaction.getAmount());
//        response.setDescription(transaction.getDescription());
//        response.setStatus(transaction.getStatus());
//        response.setDateTime(transaction.getDateTime().toString());
//        response.setChatDateTime(transaction.getDateTime());
//
//        response.setFromGlobalPotId(transaction.getFromGlobalPotId());
//        response.setToGlobalPotId(transaction.getToGlobalPotId());
//
//        return response;
//    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionResponse> toTransactionsResponse(
            final List<Transaction> transactions) {
        List<TransactionResponse>
                responses = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            responses.add(toTransactionResponse(transaction));
        }
        return responses;
    }

    /** {@inheritDoc} */
    public TransactionEvent toTransactionEvent(
            final Transaction transaction) {
        TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setWalletId(
                transaction.getUser()
                        .getMainWallet().getMainWalletId());
        transactionEvent.setTransactionId(
                transaction.getTransactionId());
        transactionEvent.setTransactionType(
                transaction.getTransactionType());
        transactionEvent.setTransactionLevel(
                transaction.getTransactionLevel());
        transactionEvent.setFromWallet(
                transaction.getFromWallet());
        transactionEvent.setFromWalletId(
                transaction.getFromWalletId());
        transactionEvent.setToWallet(
                transaction.getToWallet());
        transactionEvent.setToWalletId(
                transaction.getToWalletId());
        transactionEvent.setAmount(
                transaction.getAmount());
        transactionEvent.setDateTime(
                transaction.getDateTime().format(DATETIME_FORMATTER));
        transactionEvent.setEmail(
                transaction.getUser().getEmail());
        transactionEvent.setUuid(
                transaction.getUser().getUuid());
        transactionEvent.setFullName(
                transaction.getUser().getFullName());
        transactionEvent.setPhoneNumber(
                transaction.getUser().getPhoneNumber());
        transactionEvent.setEmailCategory(EmailCategory.TRANSACTION);

        Double balance = null;

        try {
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                if (transaction.getToWalletId() != null
                        && transaction.getToWalletId()
                        .equals(transaction.getUser()
                                .getMainWallet().getMainWalletId())) {
                    balance = transaction.getUser()
                            .getMainWallet().getBalance();
                } else if (transaction.getToWalletId() != null) {
                    SubWallet subWallet = validations.findSubWalletIfExists(
                            transaction.getUser()
                                    .getMainWallet().getMainWalletId(),
                            transaction.getToWalletId()
                    );
                    if (subWallet != null) {
                        balance = subWallet.getBalance();
                    }
                }

            } else if (transaction
                    .getTransactionType() == TransactionType.DEBIT) {
                if (transaction.getFromWalletId() != null
                        && transaction.getFromWalletId().equals(transaction
                        .getUser().getMainWallet().getMainWalletId())) {
                    balance = transaction.getUser()
                            .getMainWallet().getBalance();
                } else if (transaction.getFromWalletId() != null) {
                    SubWallet subWallet = validations.findSubWalletIfExists(
                            transaction.getUser()
                                    .getMainWallet().getMainWalletId(),
                            transaction.getFromWalletId()
                    );
                    if (subWallet != null) {
                        balance = subWallet.getBalance();
                    }
                }
            }
        } catch (Exception e) {
            // fallback if something goes wrong
            balance = transaction.getUser()
                    .getMainWallet().getBalance();
            log.warn(
                    "Balance resolution failed for txn={}"
                            + ", falling back to mainWallet balance",
                    transaction.getTransactionId(), e);
        }

        transactionEvent.setRemainingBalance(balance);
        return transactionEvent;
    }
}




