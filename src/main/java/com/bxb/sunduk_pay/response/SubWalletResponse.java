package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Response object representing a sub-wallet with its details.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String targetDate;
    /** The icon associated with the sub-wallet. */
    private String icon;
    /** The creation timestamp of the sub-wallet. */
    private String createdAt;
    /** The risk level associated with the sub-wallet. */
    private Boolean isInvested;
    /** The percentage gain or loss of the sub-wallet. */
    private String gainOrLossPercentage;
    /** The risk level associated with the sub-wallet. */
    private String riskLevel;
    /**Indicates if the investment is canceled.*/
    private Boolean isCancelInvestment;

}

