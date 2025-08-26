package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class MainWalletRequest {
    @NonNull
    private String uuid;
    private String mainWalletId;
    private String subWalletName;
    private Double amount;

    @NonNull
    private RequestType requestType;
    private TransactionType transactionType;
    private Double targetBalance;
    private LocalDate targetDate;
    private String subWalletId;
    private String sourceWalletId;
    private String targetWalletId;
    private ActionType actionType;
    private PaymentMethod paymentMethod;
    private String walletId;
    private String transactionGroupId;

    private int page = 0;
    private int size = 10;
    private String sortBy = "dateTime";
    private String sortDirection = "DESC";
}
