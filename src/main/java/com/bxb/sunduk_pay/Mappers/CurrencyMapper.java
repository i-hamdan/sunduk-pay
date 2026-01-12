package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;

import java.util.List;
import java.util.Map;

/**
 * Mapper interface for converting CurrencyRates to response objects
 * and building currency conversion responses.
 */
public interface CurrencyMapper {
    /**
     * Builds a CurrencyResponse containing exchange
     * details and historical rates.
     * @param exchangeRate the current exchange rate
     * @param converted    the converted amount
     * @param fee          the fee applied
     * @param finalAmount  the final amount after fee
     * @param yearlyRates  list of yearly rate responses
     * @param monthlyRates list of monthly rate responses
     * @param weeklyRates  list of weekly rate responses
     * @return a CurrencyResponse object.
     */
    CurrencyResponse
    currencyResponse(double exchangeRate,
                     double converted, double fee,
                     double finalAmount,
                     List<CurrencyRatesResponse> yearlyRates,
                     List<CurrencyRatesResponse> monthlyRates,
                     List<CurrencyRatesResponse> weeklyRates);

    /**
     * Converts a list of CurrencyRates into CurrencyRatesResponse
     * filtered or grouped according to the specified TimeSeries.
     *
     * @param currencyRates list of CurrencyRates
     * @param rateKey       key to extract rate
     * @param timeSeries    type of time series (WEEKLY, MONTHLY, YEARLY)
     * @return list of CurrencyRatesResponse.
     */

    List<CurrencyRatesResponse>toCurrencyRatesResponses(
            List<Map<String, Object>> currencyRates,
            String rateKey,
            TimeSeries timeSeries
    );

    /**
     * Computes monthly average rates from a list of CurrencyRates.
     *
     * @param currencyRates list of CurrencyRates
     * @param rateKey       key to extract rate
     * @return list of CurrencyRatesResponse representing monthly averages
     * representing monthly averages.
     */
    List<CurrencyRatesResponse> toMonthlyAverageResponses(
            List<Map<String, Object>> currencyRates,
            String rateKey);


}
