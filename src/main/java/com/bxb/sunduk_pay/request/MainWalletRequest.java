package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.Duration;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.util.UpdateWalletActionType;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request object for operations related to the main wallet.
 */
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MainWalletRequest {

/** Default pagination values. */
private static final int DEFAULT_PAGE = 0;
/** Default number of records per page. */
private static final int DEFAULT_SIZE = 10;


    /***  PIN for authentication.*/
    private String mpin;
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
    private UpdateWalletActionType actionType;
   /** Payment method used in the transaction. */
    private PaymentMethod paymentMethod;
    /** Tag used in transaction. */
    private String paymentTag;
    /** Phone number for UPI contact search. */
    private String phone;
    /** UPI ID of the recipient for external transfers. */
    private String recipientUpiId;
    /** Start date for reminders. */
    private LocalDate startDate;
    /** Duration for reminders. */
    private Duration duration;
    /** Remark or note associated with the reminder. */
    private String remark;
    /** ID of the wallet involved in the transaction. */
    private String walletId;
    /** ID of the sender for phone number transactions. */
    private String senderId;
    /** ID of the receiver for phone number transactions. */
    private String receiverId;
    /** ID of the reminder for operations related to reminders. */
    private String reminderId;
    /** Page number for pagination. */
    private int page = DEFAULT_PAGE;
    /** Number of records per page for pagination. */
    private int size = DEFAULT_SIZE;
    /** Field to sort by (e.g., "dateTime", "amount"). */
    private String sortBy = "dateTime";
    /** Sort direction: ASC or DESC. */
    private String sortDirection = "DESC";
    /** FCM token for push notifications. */
    private String fcmToken;
    /**
     * Indicates whether auto payment is enabled for this reminder.
     */
    private Boolean autoPayEnabled;
    /**
     * Indicates whether auto payment requires user confirmation.
     */
    private Boolean requiresConfirmation;
    /** Contact number associated with the reminder. */
    private String contactNumber;
    /** Contact name associated with reminder. */
    private String contactName;
/** The id of the confirmation associated with the auto payment,
 *  if applicable. */
    private String confirmationId;
    /** Indicates whether the user confirms the auto payment. */
    private Boolean confirm;
}

