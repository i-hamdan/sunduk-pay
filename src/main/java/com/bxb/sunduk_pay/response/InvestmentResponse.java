package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.RiskLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
     * Unique identifier for the investment.
     */
    private String investmentId;

    /**
     * Total amount invested by the user.
     */
    private Double investedAmount; // same as pot balance

    /**
     * Total amount invested including withdrawn amount.
     */
    private Double totalInvestedAmount; // total amount invested

    /**
     * Current investment value (inside the analytical circle).
     */
    private String currentValue; // how much amount is present in pot right now

    /**
     * Total current value including withdrawn amount.
     */
    private String totalCurrentValue; // total current value
    /**
     * Total profit or loss in currency (e.g., +8900 or -1200).
     */
    private String netProfitLoss;

    /**
     * Total profit or loss including withdrawn amount.
     */
    private Double totalNetProfitLoss;

    /**
     * Profit/Loss percentage (e.g., +12.3 or -4.1).
     */
    private String profitLossPercent;

    /**Gain or loss in the last 1-month period.*/
    private Double gain1MonthPercent;

    /**Gain or loss in the last 6-month period.*/
    private Double gain6MonthsPercent;

    /**Risk level of the investment.
     * */
    private RiskLevel riskLevel;

    /**Graph data for investment performance over time.*/
    private Map<String, List<InvestmentGraphDataDTO>> withdrawalTrendsGraph;

    /**Daily investment graph data.*/
    private Map<String, List<InvestmentGraphDataDTO>> dailyInvestmentGraph;

    /**response message.*/
    private String message;

    /**Indicates if the investment is canceled.*/
    private Boolean isCancelInvestment;
}

