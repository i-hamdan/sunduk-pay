package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Response payload representing a transaction.
 * <p>
 * Includes transaction details such as IDs, type, level, payment method,
 * amount, description, status, date, and associated wallets.
 * </p>
 */
@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    /** Unique identifier for the transaction. */
    private String transactionId;

    /** Group ID if the transaction belongs to a group of transactions. */
    private String groupId;

    /** UUID of the user performing the transaction. */
    private String uuid;

    /** Main wallet ID involved in the transaction. */
    private String mainWalletId;

    /** Type of transaction (CREDIT or DEBIT). */
    private TransactionType transactionType;

    /** Level of the transaction (e.g., master or subwallet level). */
    private TransactionLevel transactionLevel;

    /** Payment method used for the transaction. */
    private PaymentMethod paymentMethod;

    /** Amount involved in the transaction. */
    private Double amount;

    /** Description or note for the transaction. */
    private String description;

    /** Transaction date as a formatted string. */
    private String date;

    /** Status of the transaction (e.g., SUCCESS, PENDING). */
    private String status;

    /** Full name of the user or related party. */
    private String fullName;

    /** Name of the source wallet. */
    private String fromWallet;

    /** ID of the source wallet. */
    private String fromWalletId;

    /** Icon associated with the source wallet. */
    private String fromWalletIcon;

    /** Name of the target wallet. */
    private String toWallet;

    /** ID of the target wallet. */
    private String toWalletId;

    /** Icon associated with the target wallet. */
    private String toWalletIcon;
}
