package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Mapper interface for Investment-related operations.
 */
public interface InvestmentMapper {

    /**
     * Converts an Investment entity to an InvestmentResponse DTO.
     *
     * @param investment The Investment entity to be converted.
     * @return The corresponding InvestmentResponse DTO.
     */
    InvestmentResponse toInvestmentResponse(
            Investment investment,
            Map<String, List<InvestmentGraphDataDTO>> graphData,
    Map<String, List<InvestmentGraphDataDTO>> dailyInvestmentGraphData);
}
