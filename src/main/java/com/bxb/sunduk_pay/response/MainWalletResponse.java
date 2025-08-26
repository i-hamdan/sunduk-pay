package com.bxb.sunduk_pay.response;

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
    private String status;
    private String uuid;
    private String mainWalletId; // main walletId
    private Double balance;
    private List<SubWalletResponse> subWallets;
    private Double targetBalance;
    private List<TransactionResponse> transactionHistory;
    private String sourceSubWalletId;
    private Double previousSourceWalletBalance;
    private Double newSourceWalletBalance;
    private Double sourceAvailableBalance;
    private String targetSubWalletId;
    private Double previousTargetWalletBalance;
    private Double newTargetWalletBalance;
    private Double targetAvailableBalance;
    private Double transferredAmount;
    private String sourceTransactionId;
    private String targetTransactionId;
    private String transactionGroupId;
    private String message;
    // for stripe use
    private String session;
    private String checkoutUrl;

}
