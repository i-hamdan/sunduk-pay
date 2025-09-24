package com.bxb.sunduk_pay.response;
import lombok.Data;

import java.time.LocalDate;

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
    /**The date by which the target balance should be achieved.*/
    private LocalDate targetDate;
    /** The icon associated with the sub-wallet. */
    private String icon;
}

