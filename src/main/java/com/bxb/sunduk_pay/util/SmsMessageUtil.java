package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import org.springframework.stereotype.Component;

/**
 * Utility component for building SMS messages for transaction events.
 *
 * <p>This class generates user-friendly SMS text for different transaction types:
 * <ul>
 *   <li>Credits (internal or external)</li>
 *   <li>Debits (internal or external)</li>
 * </ul>
 * </p>
 */
@Component
public class SmsMessageUtil {

    /**
     * Builds an SMS message for a transaction event.
     *
     * @param event the transaction event
     * @return formatted SMS message
     */
    public String buildTransactionSms(final TransactionEvent event) {
        final int shortLength = 6;

        String firstName = event.getFullName() != null
                ? event.getFullName().split(" ")[0]
                : "User";

        String amount = String.format("%.0f", event.getAmount());
        String shortTxnId = event.getTransactionId().substring(0, shortLength);

        String message;

        if (event.getTransactionType() == TransactionType.CREDIT) {
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                message = firstName + ", +" + amount + " received in "
                        + event.getToWallet()
                        + ". From: " + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                message = firstName + ", +" + amount + " added to "
                        + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        } else {
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                message = firstName + ", -" + amount + " sent from "
                        + event.getFromWallet()
                        + ". To: " + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                message = firstName + ", -" + amount + " paid from "
                        + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        }

        return message;
    }
}
