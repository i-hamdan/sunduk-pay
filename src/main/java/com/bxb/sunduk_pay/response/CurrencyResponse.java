package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Response payload for currency conversion operations.
 * <p>
 * Contains the exchange rate, converted amount, conversion fee, final amount,
 * and historical rates (weekly, monthly, yearly) for display or analysis.
 * </p>
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyResponse {

    /** The current exchange rate between source and target currency. */
    private double exchangeRate;

    /** The converted amount based on the input value and exchange rate. */
    private double convertedAmount;

    /** The fee applied during the conversion process. */
    private double conversionFee;

    /** The final amount after applying the conversion fee. */
    private double finalAmount;

    /** List of currency rates for the past week. */
    private List<CurrencyRatesResponse> weeklyRates;

    /** List of currency rates for the past month. */
    private List<CurrencyRatesResponse> monthlyRates;

    /** List of currency rates for the past year. */
    private List<CurrencyRatesResponse> yearlyRates;
}
