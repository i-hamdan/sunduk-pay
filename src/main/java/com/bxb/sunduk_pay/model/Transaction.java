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

    @Id
    private String transactionId;

    /**
     * Optional group ID to associate multiple transactions together.
     */
    private String groupId;

    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private PaymentMethod paymentMethod;

    /**
     * Amount of the transaction.
     */
    private Double amount;

    /**
     * Optional description of the transaction.
     */
    private String description;

    /**
     * Status of the transaction (e.g., PENDING, SUCCESS, FAILED).
     */
    private String status;

    /**
     * Stripe payment intent ID for tracking payments via Stripe.
     */
    private String stripePaymentIntentId;

    /**
     * Date and time when the transaction was created.
     */
    private LocalDateTime dateTime;

    @DBRef
    private User user;

    @DBRef
    private MainWallet mainWallet;

    private String fromWallet;
    private String fromWalletId;
    private String toWallet;
    private String toWalletId;

    /**
     * Indicates whether this transaction belongs to the master wallet.
     */
    private boolean isMaster;
}
