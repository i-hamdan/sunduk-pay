package com.bxb.sunduk_pay.response;
import lombok.Data;
@Data
/**
 * Response object representing a sub-wallet with its details.
 */
public class SubWalletResponse {
    private String subWalletId;
    private String subWalletName;
    private Double balance;
    private Double targetBalance;
    private String icon;
}
