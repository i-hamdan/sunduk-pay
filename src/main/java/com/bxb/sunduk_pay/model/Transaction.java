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
 * Represents a financial transaction within the system.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    /**
     * Unique identifier for the transaction.
     */
    @Id
    private String transactionId;

    /**
     * Identifier for the associated group.
     */
    private String groupId;

    /**
     * Type of the transaction (e.g., credit, debit).
     */
    private TransactionType transactionType;

    /**
     * Level of the transaction (e.g., master, sub).
     */
    private TransactionLevel transactionLevel;

    /**
     * Method of payment used for the transaction.
     */
    private PaymentMethod paymentMethod;

    /**
     * Amount involved in the transaction.
     */
    private Double amount;

    /**
     * Description of the transaction.
     */
    private String description;

    /**
     * Status of the transaction.
     */
    private String status;

    /**
     * Stripe payment intent ID for this transaction.
     */
    private String stripePaymentIntentId;

    /**
     * Date and time when the transaction occurred.
     */
    private LocalDateTime dateTime;

    /**
     * Reference to the user who performed the transaction.
     */
    @DBRef
    private User user;

    /**
     * Reference to the main wallet involved in the transaction.
     */
    @DBRef
    private MainWallet mainWallet;

    /**
     * Name of the wallet from which the transaction originated.
     */
    private String fromWallet;

    /**
     * ID of the wallet from which the transaction originated.
     */
    private String fromWalletId;

    /**
     * Name of the wallet receiving the transaction.
     */
    private String toWallet;

    /**
     * ID of the wallet receiving the transaction.
     */
    private String toWalletId;

    /**
     * Indicates if the transaction is performed by the master wallet.
     */
    private boolean isMaster;
}
