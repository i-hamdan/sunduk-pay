package com.bxb.sunduk_pay.response;
import lombok.Data;
/**
 * Response object representing a sub-wallet with its details.
 */
@Data
public class SubWalletResponse {
    /** The unique identifier of the sub-wallet. */
    private String subWalletId;
    /** The name of the sub-wallet. */
    private String subWalletName;
    /** The current balance of the sub-wallet. */
    private Double balance;
    /** The target balance for the sub-wallet. */
    private Double targetBalance;
    /** The icon associated with the sub-wallet. */
    private String icon;
}

