package com.bxb.sunduk_pay.response;

import lombok.Data;

@Data
public class SubWalletResponse {
    private String subWalletId;
    private String subWalletName;
    private Double balance;
    private Double targetBalance;
}
