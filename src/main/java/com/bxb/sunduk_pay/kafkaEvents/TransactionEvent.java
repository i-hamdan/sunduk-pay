package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kafka event carrying details about a wallet transaction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    /** Unique transaction identifier. */
    private String transactionId;

    /** The wallet ID where this transaction occurred. */
    private String walletId;

    /** Transaction amount. */
    private Double amount;

    /** Transaction type (DEBIT or CREDIT). */
    private TransactionType transactionType;

    /** Transaction level (e.g., MASTER, SUBWALLET). */
    private TransactionLevel transactionLevel;

    /** Name of the sender wallet. */
    private String fromWallet;

    /** ID of the sender wallet. */
    private String fromWalletId;

    /** Name of the recipient wallet. */
    private String toWallet;

    /** ID of the recipient wallet. */
    private String toWalletId;

    /** Date and time when the transaction occurred. */
    private LocalDateTime dateTime;

    /** Remaining balance after this transaction. */
    private Double remainingBalance;

    /** User UUID associated with the transaction. */
    private String uuid;

    /** User email. */
    private String email;

    /** User full name. */
    private String fullName;

    /** User phone number. */
    private String phoneNumber;
}
