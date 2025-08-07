package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
public class WalletResponse {
    private String uuid;
    private String walletId; // main walletId
    private List<SubWallet> subWallets;
    private Double balance;
    private Double TargetBalance;
    private List<TransactionResponse> transactionHistory;
}
