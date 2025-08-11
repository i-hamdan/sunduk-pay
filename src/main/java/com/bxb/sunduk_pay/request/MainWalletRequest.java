package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
    private String subWalletId;
    private String sourceSubWalletId;
    private String targetSubWalletId;
    private ActionType actionType;
    private PaymentMethod paymentMethod;

    private int page = 0;
    private int size = 10;
    private String sortBy = "dateTime";
    private String sortDirection = "DESC";
}
