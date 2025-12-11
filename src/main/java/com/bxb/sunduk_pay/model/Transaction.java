package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

/**
 * Represents a financial transaction in the system.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    /**
     * Unique identifier for the transaction.
     */
    @Id
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
     * Remaining balance after the transaction.
     */
    private Double remainingBalance;
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
     * The Tag of the transaction.
     */
    private String paymentTag;
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
    /**
     * UPI ID of the recipient in the transaction.
     */
    private String recipientUpiId;
    /**
     *  Investment associated with the transaction.
     */
    @ManyToOne
    @JoinColumn(name = "investment_id")
    private Investment investment;
    /**
     * Monthly profit or loss associated
     */
    @Column
    private Double monthlyProfitLoss;

    /**
     * Indicates if the associated sub-wallet
     * is invested.
     */
    @Column(nullable = false)
    private Boolean isInvestment;
    /**
     * Risk level associated with the transaction.
     */

    private RiskLevel riskLevel;
}
