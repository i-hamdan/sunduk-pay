package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Represents a request for main wallet operations.
 * <p>
 * Can be used for creating/updating wallets, performing transactions,
 * filtering paginated results, and specifying transaction details.
 * </p>
 */
@NoArgsConstructor
@Data
public class MainWalletRequest {

    /** User UUID (required). */
    @NonNull
    private String uuid;

    /** Main wallet ID. */
    private String mainWalletId;

    /** Name of the sub-wallet. */
    private String subWalletName;

    /** Amount for the transaction . */
    private Double amount;

    /** Type of request (required). */
    @NonNull
    private RequestType requestType;

    /** Transaction type. */
    private TransactionType transactionType;

    /** Target balance for the wallet. */
    private Double targetBalance;

    /** Target date to achieve the target balance (optional). */
    private LocalDate targetDate;

    /** Sub-wallet ID (optional). */
    private String subWalletId;

    /** Icon associated with the wallet (optional). */
    private String icon;

    /** Source wallet ID for transfers (optional). */
    private String sourceWalletId;

    /** Target wallet ID for transfers (optional). */
    private String targetWalletId;

    /** Action type for the operation (optional). */
    private ActionType actionType;

    /** Payment method used (optional). */
    private PaymentMethod paymentMethod;

    /** Wallet ID for specific queries (optional). */
    private String walletId;

    /** Transaction group ID (optional). */
    private String transactionGroupId;

    /** Pagination: page number (default 0). */
    private int page = 0;

    /** Pagination: page size (default 10). */
    private int size = 10;

    /** Sorting field (default "dateTime"). */
    private String sortBy = "dateTime";

    /** Sorting direction (default "DESC"). */
    private String sortDirection = "DESC";
}
