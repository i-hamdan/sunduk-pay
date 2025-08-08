package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import lombok.NonNull;

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
    private ActionType actionType;
}
