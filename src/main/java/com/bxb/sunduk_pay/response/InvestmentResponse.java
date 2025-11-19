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
public class InvestmentResponse {
    /**
     * Sub-wallet information related to the investment.
     */
    private SubWalletResponse subWallet;

    /**
     * Total amount invested by the user.
     */
    private double investedAmount; // same ass pot balance
    /**
     *
     */
    private double progressPercentage;// how much amount left for fullfill pot

    /**
     * Current investment value (inside the analytical circle)
     */
    private Double currentValue; // how much amount is present in pot right now

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

