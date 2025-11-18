package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * InvestmentResponse represents the response structure for
 * investment-related API calls.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class subWalletInvestmentResponse {
    /**
     * Sub-wallet information related to the investment.
     */
    private SubWalletResponse subWallet;

    /**
     * Current investment value (inside the analytical circle)
     */
    private Double currentValue;

    /**
     * Total profit or loss in currency (e.g., +8900 or -1200)
     */
    private Double netProfitLoss;

    /**
     * Profit/Loss percentage (e.g., +12.3 or -4.1)
     */
    private Double profitLossPercent;

    /**
     * Last month increase/decrease in portfolio (displayed above graph)
     */
    private Double lastMonthChange;

}
