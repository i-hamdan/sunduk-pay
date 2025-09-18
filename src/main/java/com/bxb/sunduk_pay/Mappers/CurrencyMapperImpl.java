package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CurrencyMapper}.
 */
@Component
public  class CurrencyMapperImpl implements CurrencyMapper {

    /**
     * Formatter for displaying days in short format (e.g. 05 Jan).
     */
    private static final DateTimeFormatter DAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM");

    /**
     * Formatter for displaying months in format (e.g. Jan 2024).
     */
    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMM yyyy");

    @Override
    public CurrencyResponse currencyResponse(
            final double exchangeRate,
            final double convertedAmount,
            final double conversionFee,
            final double finalAmount,
            final List<CurrencyRatesResponse> yearlyRates,
            final List<CurrencyRatesResponse> monthlyRates,
            final List<CurrencyRatesResponse> weeklyRates
    ) {
        CurrencyResponse response = new CurrencyResponse();
        response.setExchangeRate(exchangeRate);
        response.setConvertedAmount(convertedAmount);
        response.setConversionFee(conversionFee);
        response.setFinalAmount(finalAmount);
        response.setYearlyRates(yearlyRates);
        response.setMonthlyRates(monthlyRates);
        response.setWeeklyRates(weeklyRates);
        return response;
    }

    @Override
    public List<CurrencyRatesResponse> toCurrencyRatesResponses(
            final List<CurrencyRates> currencyRates,
            final String rateKey,
            final TimeSeries timeSeries
    ) {
        if (currencyRates == null || currencyRates.isEmpty()) {
            return Collections.emptyList();
        }

        return currencyRates.stream()
                .map(rate -> mapToCurrencyRatesResponse(rate, rateKey, timeSeries))
                .collect(Collectors.toList());
    }

    private CurrencyRatesResponse mapToCurrencyRatesResponse(
            final CurrencyRates currencyRates,
            final String rateKey,
            final TimeSeries timeSeries
    ) {
        CurrencyRatesResponse response = new CurrencyRatesResponse();
        response.setDate(currencyRates.getDate());
        Double value = currencyRates.getRates().get(rateKey);
        response.setValue(value);

        if (timeSeries != null) {
            switch (timeSeries) {
                case WEEK ->
                        response.setDay(currencyRates.getDate().format(DAY_FORMATTER));
                case MONTH ->
                        response.setDayMonth(currencyRates.getDate().format(DAY_FORMATTER));
                default -> {
                    // no-op
                }
            }
        }

        return response;
    }

    @Override
    public List<CurrencyRatesResponse> toMonthlyAverageResponses(
            final List<CurrencyRates> currencyRates,
            final String rateKey
    ) {
        if (currencyRates == null || currencyRates.isEmpty()) {
            return Collections.emptyList();
        }

        Map<YearMonth, Double> monthlyAverages = currencyRates.stream()
                .filter(r -> r.getRates().get(rateKey) != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(
                                r -> r.getRates().get(rateKey)
                        )
                ));

        List<CurrencyRatesResponse> responses = new ArrayList<>();
        monthlyAverages.forEach((yearMonth, avgValue) -> {
            CurrencyRatesResponse response = new CurrencyRatesResponse();
            response.setMonth(yearMonth.format(MONTH_FORMATTER));
            response.setValue(avgValue);
            responses.add(response);
        });

        return responses;
    }
}
