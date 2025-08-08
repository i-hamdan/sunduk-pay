package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.SubWallet;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainWalletResponse {
    private String uuid;
    private String walletId; // main walletId
    private List<SubWallet> subWallets;
    private Double balance;
    private Double TargetBalance;
    private List<TransactionResponse> transactionHistory;
    private String sourceSubWalletId;
    private Double sourceAvailableBalance;
    private String targetSubWalletId;
    private Double targetAvailableBalance;
    private Double transferredAmount;
    private String message;
}
