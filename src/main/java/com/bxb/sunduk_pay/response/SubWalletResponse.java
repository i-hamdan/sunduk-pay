package com.bxb.sunduk_pay.response;

import lombok.Data;

/**
 * Response payload representing a sub-wallet.
 * <p>
 * Contains the sub-wallet ID, name, current balance,
 * target balance, and associated icon.
 * Used in responses where sub-wallet details are required.
 * </p>
 */
@Data
public class SubWalletResponse {

    /** Unique identifier of the sub-wallet. */
    private String subWalletId;

    /** Name of the sub-wallet. */
    private String subWalletName;

    /** Current balance in the sub-wallet. */
    private Double balance;

    /** Target balance set for the sub-wallet (optional). */
    private Double targetBalance;

    /** Icon associated with the sub-wallet (optional). */
    private String icon;
}
