package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Response object representing a transaction.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    /**
     * Unique identifier for the transaction.
     */
    private String transactionId;
    /**
     * User identifier associated with the transaction.
     */
    private String uuid;
    /**
     * Type of the transaction (e.g., CREDIT, DEBIT).
     */
    private TransactionType transactionType;
    /**
     * Level of the transaction (e.g., INTERNAL,EXTERNAL).
     */
    private TransactionLevel transactionLevel;
    /**
     * Payment method used in the transaction.
     */
    private PaymentMethod paymentMethod;
    /**
     * Amount involved in the transaction.
     */
    private Double amount;
    /**
     * Description or note about the transaction.
     */
    private String description;
    /**
     * Date when the transaction occurred.
     */
    private String date;
    /**
     * Date and Time when the transaction occurred.
     */
    private String dateTime;
    /**
     * Date and time in LocalDateTime format for ui purposes.
     */
    private LocalDateTime chatDateTime;
    /**
     * Status of the transaction (e.g., Success, failure).
     */
    private String status;
    /**
     * Full name of the user associated
     * with the transaction.
     */
    private String fullName;
    /**
     * Tag of the transaction.
     */
    private String paymentTag;
    /**
     * Name of the source wallet associated
     * with the transaction.
     */
    private String fromWallet;
    /**
     * Identifier of the source wallet
     * associated with the transaction.
     */
    private String fromWalletId;

    /**
     * Identifier of the source GlobalPot
     * associated with the transaction.
     */
    private String fromGlobalPotId;
    /**
     * Phone number of the sender
     * in case of external transactions.
     */
    private String fromPhoneNumber;

    /**
     * Name of the destination wallet
     * associated with the transaction.
     */
    private String toWallet;
    /**
     * Identifier of the destination wallet
     * associated with the transaction.
     */
    private String toWalletId;
    /**
     * Phone number of the recipient
     * in case of external transactions.
     */
    private String toPhoneNumber;
    /**
     * Icon of the destination wallet.
     */
    private String toWalletIcon;
    /**
     * Icon of the source wallet.
     */
    private String fromWalletIcon;
    /**
     * Identifier of the destination GlobalPot
     * associated with the transaction.
     */
    private String toGlobalPotId;
    /**
     * UPI ID of the recipient
     * in case of external transactions.
     */
    private String recipientUpiId;


    //---fields for anonymous masking of transactions---//
    /**
     * Indicates if the transaction is anonymous.
     * can be null
     */
    private Boolean isAnonymous;

    /**
     * Indicates the Id of the anonymous sender.
     * Can only be non-null in case of anonymous transaction*/
    private String anonymousId;

    /**
     * Indicates the unique color tag of the anonymous sender.
     * Can only be non-null in case of anonymous transaction
     */
    private String anonymousColor;

    private String RiskLevel;
}

