package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Implementation of InvestmentMapper to convert Investment entities
 * into corresponding response DTOs.
 */
@Component
@RequiredArgsConstructor
public class InvestmentMapperImpl implements InvestmentMapper {

    /**
     * Formatter for decimal values.
     */
    private static final DecimalFormat FORMATTER =
            new DecimalFormat("#,##0.00");
    /**
     * Mapper for wallet-related operations.
     */
    private final WalletMapper walletMapper;

    /**
     * Converts an Investment entity into an InvestmentResponse DTO,
     * including graph data.
     *
     * @param investment          the investment entity
     * @param graphData           the withdrawal trends graph data
     * @param dailyInvestmentData the daily investment graph data
     * @return an InvestmentResponse containing investment details and graphs
     */
    @Override
    public InvestmentResponse toInvestmentResponse(
            final Investment investment,
            final Map<String, List<InvestmentGraphDataDTO>> graphData,
            final Map<String,
                    List<InvestmentGraphDataDTO>> dailyInvestmentData) {
        return InvestmentResponse.builder()
                .subWallet(walletMapper.toSubWalletResponse(
                        investment.getSubWallet()))
                .riskLevel(investment.getRiskLevel())
                .investedAmount(investment.getInvestmentAmount())
                .currentValue(FORMATTER.format(investment.getCurrentValue()))
                .netProfitLoss(FORMATTER.format(investment.getProfitLoss()))
                .profitLossPercent(
                        FORMATTER.format(investment.getProfitLossPercentage()))
                .withdrawalTrendsGraph(graphData)
                .dailyInvestmentGraph(dailyInvestmentData)
                .build();
    }
}
