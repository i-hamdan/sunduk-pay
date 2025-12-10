package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object representing a sub-wallet with its details.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubWalletResponse {
    /** The unique identifier of the sub-wallet. */
    private String subWalletId;
    /** The name of the sub-wallet. */
    private String subWalletName;
    /** The current balance of the sub-wallet. */
    private String balance;
    /** The target balance for the sub-wallet. */
    private Double targetBalance;
    /**The date by which the target balance should be achieved.*/
    private String targetDate;
    /** The icon associated with the sub-wallet. */
    private String icon;
    /** The creation timestamp of the sub-wallet. */
    private String createdAt;
    /** The risk level associated with the sub-wallet. */
    private Boolean isInvested;
    /**Profit or loss percentage*/
    private String gainOrLossPercentage;
}

