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

/** Default pagination values. */
private static final int DEFAULT_PAGE = 0;
/** Default number of records per page. */
private static final int DEFAULT_SIZE = 10;



    /** Unique identifier for the request. */
    @NonNull
    private Long uuid;
    /** ID of the main wallet. */
    private Long mainWalletId;
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
    private Long subWalletId;
  /** Icon representing the sub-wallet. */
    private String icon;
    /** ID of the source wallet for transfers. */
    private Long sourceWalletId;
    /** ID of the target wallet for transfers. */
    private Long targetWalletId;
/** Action type for Updating pot (e.g., RENAME_POT,
    GOAL_AMOUNT,
    GOAL_DATE). */
    private ActionType actionType;
   /** Payment method used in the transaction. */
    private PaymentMethod paymentMethod;
    /** ID of the wallet involved in the transaction. */
    private Long walletId;
    /** Group ID for batch transactions. */
    private Long transactionGroupId;
    /** Page number for pagination. */
    private int page = DEFAULT_PAGE;
    /** Number of records per page for pagination. */
    private int size = DEFAULT_SIZE;
    /** Field to sort by (e.g., "dateTime", "amount"). */
    private String sortBy = "dateTime";
    /** Sort direction: ASC or DESC. */
    private String sortDirection = "DESC";
}

