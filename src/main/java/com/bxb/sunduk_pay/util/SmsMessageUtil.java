package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import org.springframework.stereotype.Component;

/**
 * Utility component for building SMS messages for transaction events.
 *
 * <p>This class generates user-friendly
 * SMS text for different transaction types:
 * <ul>
 *   <li>Credits (internal or external)</li>
 *   <li>Debits (internal or external)</li>
 * </ul>
 * </p>
 */
@Component
public class SmsMessageUtil {

    /** Number of characters to show for transaction ID. */
    private static final int TXN_ID_TRIM_LENGTH = 6;

    /**
     * Builds an SMS message for a transaction event.
     *
     * @param event the transaction event
     * @return formatted SMS message
     */
    public String buildTransactionSms(final TransactionEvent event) {
        String firstName = event.getFullName() != null
                ? event.getFullName().split(" ")[0]
                : "User";

        String amount = String.format(
                "%.0f", event.getAmount());
        String shortTxnId = event
                .getTransactionId()
                .substring(0, TXN_ID_TRIM_LENGTH);

        String message;

        // Handle CREDIT
        if (event.getTransactionType() == TransactionType.CREDIT) {
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                // Internal Credit (sub <-> main, sub <-> sub)
                message = firstName + ", +" + amount + " received in "
                        + event.getToWallet()
                        + ". From: " + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                // External Credit (bank/other user -> my wallet)
                message = firstName + ", +" + amount + " added to "
                        + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        } else {
            // Handle DEBIT
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                // Internal Debit (sub <-> main, sub <-> sub)
                message = firstName + ", -" + amount + " sent from "
                        + event.getFromWallet()
                        + ". To: " + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                // External Debit (my wallet -> bank/other user)
                message = firstName + ", -" + amount + " paid from "
                        + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        }

        return message;
    }
    /**
     * Builds an SMS message for OTP verification.
     *
     * @param otp the one-time password
     * @return formatted SMS message
     */
    public String buildOtpSms(final String otp) {
        return String.format(
                "Your SundukPay verification code is: %s. Valid for 60 seconds. Do not share this code with anyone.",
                otp
        );
    }
}
