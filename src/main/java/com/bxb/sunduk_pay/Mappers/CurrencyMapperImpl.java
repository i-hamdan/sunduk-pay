package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Implementation of CurrencyMapper to map currency data to response objects.
 */
@Component
public class CurrencyMapperImpl implements CurrencyMapper {

    private static final DecimalFormat formatter =
            new DecimalFormat("#,##0.00");



    /**
     * Maps currency conversion details and rate
     * lists to a CurrencyResponse object.
     * @param exchangeRate the exchange rate used for conversion
     * @param converted the converted amount
     * @param fee the conversion fee
     * @param finalAmount the final amount after conversion and fee
     * @param yearlyRates list of yearly currency rates responses
     * @param monthlyRates list of monthly currency rates responses
     * @param weeklyRates list of weekly currency rates responses
     * @return a populated CurrencyResponse object.
     */

    @Override
    public CurrencyResponse currencyResponse(
            final double exchangeRate,
            final double converted,
            final double fee,
            final double finalAmount,
            final List<CurrencyRatesResponse> yearlyRates,
            final List<CurrencyRatesResponse> monthlyRates,
            final List<CurrencyRatesResponse> weeklyRates) {
        CurrencyResponse response = new CurrencyResponse();
        response.setExchangeRate(exchangeRate);
        response.setConvertedAmount(converted);
        response.setConversionFee(fee);

        response.setFinalAmount(formatter
                .format(finalAmount));
        response.setYearlyRates(yearlyRates);
        response.setMonthlyRates(monthlyRates);
        response.setWeeklyRates(weeklyRates);
        return response;
    }

    /**
     * Converts a list of CurrencyRates to
     * a list of CurrencyRatesResponse objects.
     *
     * @param currencyRates the list of CurrencyRates to convert
     * @param rateKey the key to extract the rate value
     * @param timeSeries the time series granularity (WEEK, MONTH, etc.)
     * @return a list of CurrencyRatesResponse objects.
     */
    public List<CurrencyRatesResponse> toCurrencyRatesResponses(
            final List<CurrencyRates> currencyRates,
            final String rateKey,
            final TimeSeries timeSeries) {
        List<CurrencyRatesResponse> list = new ArrayList<>();
        for (CurrencyRates rates : currencyRates) {
            list.add(toCurrencyRatesResponse(rates, rateKey, timeSeries));
        }
        return list;
    }

    /**
     * Converts a single CurrencyRates object to a CurrencyRatesResponse.
     *
     * @param currencyRates the CurrencyRates object to convert
     * @param rateKey the key to extract the rate value
     * @param timeSeries the time series granularity
     * @return a CurrencyRatesResponse object.
     */
    private CurrencyRatesResponse
    toCurrencyRatesResponse(
    final CurrencyRates currencyRates,
    final String rateKey,
    final TimeSeries timeSeries) {

        CurrencyRatesResponse currencyRatesResponse =
                new CurrencyRatesResponse();
        currencyRatesResponse.setDate(currencyRates.getDate());
        Double value = currencyRates.getRates().get(rateKey);
        currencyRatesResponse.setValue(value);
        switch (timeSeries) {
            case WEEK -> {
             String day = currencyRates.getDate()
            .format(DateTimeFormatter.ofPattern("dd MMM"));
            currencyRatesResponse.setDay(day);
            }
            case MONTH -> {
                String formatted = currencyRates.getDate()
               .format(DateTimeFormatter.ofPattern("dd MMM"));
                currencyRatesResponse.setDayMonth(formatted);
            }
            default -> throw new ResourceNotFoundException(
                    "Unsupported TimeSeries: " + timeSeries);

        }
        return currencyRatesResponse;
    }
    /**
     * Calculates monthly average rates and maps them.
     * to CurrencyRatesResponse objects
     * @param currencyRates the list of CurrencyRates to process
     * @param rateKey the key to extract the rate value
     * @return a list of CurrencyRatesResponse objects with monthly averages.
     */
    public List<CurrencyRatesResponse> toMonthlyAverageResponses(
            final List<CurrencyRates> currencyRates,
    final String rateKey) {

        Map<YearMonth, Double> monthlyAverages = currencyRates.stream()
                .filter(r -> r.getRates().get(rateKey) != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(
                                r -> r.getRates().get(rateKey))
                ));

        List<CurrencyRatesResponse> list = new ArrayList<>();
        monthlyAverages.forEach(
                (yearMonth, avgValue) -> {
       CurrencyRatesResponse res = new CurrencyRatesResponse();
       res.setMonth(
       yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")));
       res.setValue(avgValue);
            list.add(res);
        });
        return list;
    }
}
