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
 * Request object for operations related to the main wallet.
 */
@NoArgsConstructor
@Data
public class MainWalletRequest {
    /** Unique identifier for the request. */
    @NonNull
    private String uuid;
    /** ID of the main wallet. */
    private String mainWalletId;
    /** Name of the sub-wallet. */
    private String subWalletName;
    /** Amount for transactions or operations. */
    private Double amount;
    /** Type of request being made. */
    @NonNull
    private RequestType requestType;
    /** Type of transaction (e.g., CREDIT,DEBIT). */
    private TransactionType transactionType;
  /** Description of the transaction. */
    private Double targetBalance;
    /** Target date for scheduled transactions or goals. */
    private LocalDate targetDate;
   /** ID of the sub-wallet. */
    private String subWalletId;
  /** Icon representing the sub-wallet. */
    private String icon;
    /** ID of the source wallet for transfers. */
    private String sourceWalletId;
    /** ID of the target wallet for transfers. */
    private String targetWalletId;
/** Action type for Updating pot (e.g., RENAME_POT,
    GOAL_AMOUNT,
    GOAL_DATE). */
    private ActionType actionType;
   /** Payment method used in the transaction. */
    private PaymentMethod paymentMethod;
   /** Start date for filtering transactions. */
    private String walletId;
    /** Description or note for the transaction. */
    private String transactionGroupId;
    /** Page number for pagination. */
    private int page = 0;
    /** Number of records per page for pagination. */
    private int size = 10;
    /** Field to sort by (e.g., "dateTime", "amount"). */
    private String sortBy = "dateTime";
    /** Sort direction: ASC or DESC. */
    private String sortDirection = "DESC";
}

