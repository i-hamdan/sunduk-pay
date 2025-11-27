package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
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
     * Total amount invested by the user.
     */
    private Double investedAmount; // same as pot balance

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

    /**Graph data for investment performance over time.*/
    private Map<String, List<InvestmentGraphDataDTO>> monthlyGraphData;

    /**response message.*/
    private String message;
}

