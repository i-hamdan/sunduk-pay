package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.EmailCategory;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a transaction event in the SundukPay system.
 * This class is used for Kafka messaging to convey transaction details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    /**
     * Unique identifier for the transaction.
     */
    private String transactionId;

    /**
     * Identifier for the wallet.
     */
    private String walletId;

    /**
     * Amount involved in the transaction.
     */
    private Double amount;

    /**
     * Description of the transaction.
     */
    private TransactionType transactionType;

    /**
     * Level of the transaction (e.g., master, sub).
     */
    private TransactionLevel transactionLevel;

    /**
     * Source wallet for the transaction.
     */
    private String fromWallet;

    /**
     * Unique identifier for the source wallet.
     */
    private String fromWalletId;

    /**
     * Target wallet for the transaction.
     */
    private String toWallet;

    /**
     * Unique identifier for the target wallet.
     */
    private String toWalletId;

    /**
     * Timestamp when the transaction occurred.
     */
    private String dateTime;

    /**
     * Balance remaining after the transaction.
     */
    private Double remainingBalance;

    /**
     * Unique identifier for the user associated with the transaction.
     */
    private String uuid;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's full name.
     */
    private String fullName;

    /**
     * The user's phone number.
     */
    private String phoneNumber;

    /**
     * Category of email to be sent related to the transaction.
     */
    private EmailCategory emailCategory;
}
