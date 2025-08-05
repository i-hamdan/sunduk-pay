package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
public class WalletResponse {
    private UserResponse user;
    private String walletId;
    private Double totalBalance;
    private Double reservedBalance;
    private Double availableBalance;
    private List<SubWalletResponse> subWallets;
}
