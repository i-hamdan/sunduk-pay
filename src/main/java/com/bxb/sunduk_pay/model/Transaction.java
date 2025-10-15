package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Represents a financial transaction in the system.
 */
public class Transaction {
    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    /**
     * User associated with the transaction.
     */
    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    /**
     * Type of the transaction.
     */
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    /**
     * Level of the transaction.
     */
    @Enumerated(EnumType.STRING)
    private TransactionLevel transactionLevel;
    /**
     * Payment method used in the transaction.
     */
    @Enumerated(EnumType.STRING)
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
     * Id of Stripe payment intent associated
     * with the transaction.
     */
    private String stripePaymentIntentId;
    /**
     * Timestamp when the transaction was created.
     */
    private LocalDateTime dateTime;
    /**
     * Indicates if the transaction is
     * associated with a master wallet.
     */
    @Column(nullable = false)
    private Boolean isMaster;
    /**
     * Name of the sender in the transaction.
     */
    private String fromWallet;
    /**
     * ID of the sender's wallet in the transaction.
     */
    private String fromWalletId;
    /**
     * Phone number of the sender in the transaction.
     */
    private String fromPhoneNumber;
    /**
     * Name of the receiver in the transaction.
     */
    private String toWallet;
    /**
     * ID of the receiver's wallet in the transaction.
     */
    private String toWalletId;
    /**
     * Phone number of the recipient in the transaction.
     */
    private String toPhoneNumber;
}