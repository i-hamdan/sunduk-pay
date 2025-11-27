package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvestmentMapperImpl implements InvestmentMapper{

    /**
     * Mapper for wallet-related operations.
     */
    private final WalletMapper walletMapper;

    @Override
    public InvestmentResponse toInvestmentResponse(Investment investment ,
   Map<String,List<InvestmentGraphDataDTO>> graphData) {
        return InvestmentResponse.builder()
                .subWallet(walletMapper.toSubWalletResponse(investment.getSubWallet()))
                .investedAmount(investment.getInvestmentAmount())
                .currentValue(investment.getCurrentValue())
                .netProfitLoss(investment.getProfitLoss())
                .profitLossPercent(investment.getProfitLossPercentage())
                .monthlyGraphData(graphData)
                .build();
    }
}
