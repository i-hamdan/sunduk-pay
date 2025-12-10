package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvestmentMapperImpl implements InvestmentMapper {

    /**
     * Formatter for decimal values.
     */
    private static final DecimalFormat formatter =
            new DecimalFormat("#,##0.00");
    /**
     * Mapper for wallet-related operations.
     */
    private final WalletMapper walletMapper;

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
                .currentValue(formatter.format(investment.getCurrentValue()))
                .netProfitLoss(formatter.format(investment.getProfitLoss()))
                .profitLossPercent(
                        formatter.format(investment.getProfitLossPercentage()))
                .withdrawalTrendsGraph(graphData)
                .dailyInvestmentGraph(dailyInvestmentData)
                .build();
    }
}
