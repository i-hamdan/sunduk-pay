package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CurrencyMapperImpl implements CurrencyMapper {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

    @Override
    public CurrencyResponse currencyResponse(
            double exchangeRate,
            double convertedAmount,
            double conversionFee,
            double finalAmount,
            List<CurrencyRatesResponse> yearlyRates,
            List<CurrencyRatesResponse> monthlyRates,
            List<CurrencyRatesResponse> weeklyRates
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
            List<CurrencyRates> currencyRates,
            String rateKey,
            TimeSeries timeSeries
    ) {
        if (currencyRates == null || currencyRates.isEmpty()) {
            return Collections.emptyList();
        }

        return currencyRates.stream()
                .map(rate -> mapToCurrencyRatesResponse(rate, rateKey, timeSeries))
                .collect(Collectors.toList());
    }

    private CurrencyRatesResponse mapToCurrencyRatesResponse(
            CurrencyRates currencyRates,
            String rateKey,
            TimeSeries timeSeries
    ) {
        CurrencyRatesResponse response = new CurrencyRatesResponse();
        response.setDate(currencyRates.getDate());
        Double value = currencyRates.getRates().get(rateKey);
        response.setValue(value);

        if (timeSeries != null) {
            switch (timeSeries) {
                case WEEK -> response.setDay(currencyRates.getDate().format(DAY_FORMATTER));
                case MONTH -> response.setDayMonth(currencyRates.getDate().format(DAY_FORMATTER));
            }
        }

        return response;
    }

    @Override
    public List<CurrencyRatesResponse> toMonthlyAverageResponses(
            List<CurrencyRates> currencyRates,
            String rateKey
    ) {
        if (currencyRates == null || currencyRates.isEmpty()) {
            return Collections.emptyList();
        }

        Map<YearMonth, Double> monthlyAverages = currencyRates.stream()
                .filter(r -> r.getRates().get(rateKey) != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(r -> r.getRates().get(rateKey))
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
