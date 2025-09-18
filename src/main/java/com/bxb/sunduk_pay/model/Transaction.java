package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a transaction in the system.
 * Stores details about the payment, type, level, wallets, and user.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /** Unique identifier for the transaction. */
    @Id
    private String transactionId;

    /** Optional group ID to associate multiple transactions together. */
    private String groupId;

    /** Type of transaction (e.g., CREDIT, DEBIT). */
    private TransactionType transactionType;

    /** Level of the transaction (e.g., MASTER, SUB). */
    private TransactionLevel transactionLevel;

    /** Payment method used for the transaction. */
    private PaymentMethod paymentMethod;

    /** Amount of the transaction. */
    private Double amount;

    /** Optional description of the transaction. */
    private String description;

    /** Status of the transaction (e.g., PENDING, SUCCESS, FAILED). */
    private String status;

    /** Stripe payment intent ID for tracking payments via Stripe. */
    private String stripePaymentIntentId;

    /** Date and time when the transaction was created. */
    private LocalDateTime dateTime;

    /** User associated with the transaction. */
    @DBRef
    private User user;

    /** Main wallet associated with the transaction. */
    @DBRef
    private MainWallet mainWallet;

    /** Name of the source wallet for the transaction. */
    private String fromWallet;

    /** ID of the source wallet. */
    private String fromWalletId;

    /** Name of the destination wallet for the transaction. */
    private String toWallet;

    /** ID of the destination wallet. */
    private String toWalletId;

    /** Indicates whether this transaction belongs to the master wallet. */
    private boolean isMaster;
}
