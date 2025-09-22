package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response object representing a transaction.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
 /** Unique identifier for the transaction. */
    private String transactionId;
    /** Group identifier for related transactions. */
    private String groupId;
    /** User identifier associated with the transaction. */
    private String uuid;
    /** Identifier of the main wallet associated with the transaction. */
    private String mainWalletId;
    /** Type of the transaction (e.g., CREDIT, DEBIT). */
    private TransactionType transactionType;
  /** Level of the transaction (e.g., INTERNAL,EXTERNAL). */
    private TransactionLevel transactionLevel;
   /** Payment method used in the transaction. */
    private PaymentMethod paymentMethod;

    /** Amount involved in the transaction. */
    private Double amount;
    /** Description or note about the transaction. */
    private String description;
    /** Date and time when the transaction occurred. */
    private String date;
  /** Status of the transaction (e.g., Success, failure). */
    private String status;
    /** Full name of the user associated
     *  with the transaction. */
    private String fullName;
    /** Name of the source wallet associated
     * with the transaction. */
    private String fromWallet;
    /** Identifier of the source wallet
     *  associated with the transaction. */
    private String fromWalletId;
    /** Name of the destination wallet
     * associated with the transaction. */
    private String toWallet;
   /** Identifier of the destination wallet
    *  associated with the transaction. */
    private String toWalletId;
   /** Icon of the destination wallet. */
    private String toWalletIcon;
    /** Icon of the source wallet. */
    private String fromWalletIcon;
}

