package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageUtil {
    public String buildTransactionSms(TransactionEvent event) {
        String firstName = event.getFullName() != null
                ? event.getFullName().split(" ")[0]
                : "User";

        String amount = String.format("%.0f", event.getAmount());
//        String balance = String.format("%.0f", event.getRemainingBalance());
        String shortTxnId = event.getTransactionId().substring(0, 6);

        String message;

        // Handle CREDIT
        if (event.getTransactionType() == TransactionType.CREDIT) {
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                // Internal Credit (sub ↔ main, sub ↔ sub)
                message = firstName + ", +" + amount + " received in "
                        + event.getToWallet()
                        + ". From: " + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                // External Credit (bank/other user → my wallet)
                message = firstName + ", +" + amount + " added to "
                        + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        }
        // Handle DEBIT
        else {
            if (event.getTransactionLevel() == TransactionLevel.INTERNAL) {
                // Internal Debit (sub ↔ main, sub ↔ sub)
                message = firstName + ", -" + amount + " sent from "
                        + event.getFromWallet()
                        + ". To: " + event.getToWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            } else {
                // External Debit (my wallet → bank/other user)
                message = firstName + ", -" + amount + " paid from "
                        + event.getFromWallet()
                        + ". Txn:" + shortTxnId
                        + ". -SundukPay";
            }
        }

        return message;
    }

}
