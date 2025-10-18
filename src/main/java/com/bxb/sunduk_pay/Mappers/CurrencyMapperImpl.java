package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.springframework.stereotype.Component;

import java.sql.Date;
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
    /**
     * Decimal formatter for formatting final amounts with
     * two decimal places.
     */
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
 * Maps a list of currency rate data to CurrencyRatesResponse.
 * objects based on the specified time series (WEEKLY or MONTHLY).
 *  * @param currencyRates list of maps containing currency rate data
 * @param rateKey the key to extract the rate from each map
 * @param timeSeries the time series type (WEEKLY or MONTHLY)
 * @return a list of CurrencyRatesResponse objects.

 */
    @Override
    public List<CurrencyRatesResponse> toCurrencyRatesResponses(
            final List<Map<String, Object>> currencyRates,
            final String rateKey,
            final TimeSeries timeSeries) {
        List<CurrencyRatesResponse> response =
                currencyRates
                .stream().map(row -> {
            CurrencyRatesResponse r =
                    new CurrencyRatesResponse();
            r.setDate(((java.sql.Date)row
                    .get("date")).toLocalDate());
            r.setValue((Double) row.get("rate"));
        switch (timeSeries){
            case WEEK ->
                r.setDay(r.getDate()
              .format(java.time.format
             .DateTimeFormatter.ofPattern("dd MMM")));

            case MONTH ->
                r.setDayMonth(r.getDate()
                        .format(DateTimeFormatter
                                .ofPattern("dd MMM")));

            default ->
                throw new ResourceNotFoundException(
                        "Unsupported TimeSeries: " + timeSeries);

        }
        return r;
        }).toList();

        return response;
    }

    /**
     * Computes monthly average currency rates from a list of rate data.
     * @param currencyRates list of maps containing currency rate data
     * @param rateKey the key to extract the rate from each map
     * @return a list of CurrencyRatesResponse objects representing monthly averages.
     */

    @Override
    public List<CurrencyRatesResponse> toMonthlyAverageResponses(
            final List<Map<String, Object>> currencyRates,
            final String rateKey) {
      Map<YearMonth, Double> monthlyAverages =
              currencyRates.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth
                                .from(((java.sql.Date) r.get("date"))
                                        .toLocalDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(
                                r ->
                                        (Double) r.get("rate"))
                ));

        List<CurrencyRatesResponse> response = new ArrayList<>();
        monthlyAverages.forEach((ym, avg) -> {
            CurrencyRatesResponse res = new CurrencyRatesResponse();
            res.setMonth(ym
                    .format(DateTimeFormatter.ofPattern("MMM yyyy")));
            res.setValue(avg);
            response.add(res);
        });

        return response;
    }
}

