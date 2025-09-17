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

    @Override
    public CurrencyResponse currencyResponse(double exchangeRate, double converted, double fee, double finalAmount, List<CurrencyRatesResponse> yearlyRates, List<CurrencyRatesResponse> monthlyRates, List<CurrencyRatesResponse> weeklyRates) {
        CurrencyResponse response = new CurrencyResponse();
        response.setExchangeRate(exchangeRate);
        response.setConvertedAmount(converted);
        response.setConversionFee(fee);
        response.setFinalAmount(finalAmount);
        response.setYearlyRates(yearlyRates);
        response.setMonthlyRates(monthlyRates);
        response.setWeeklyRates(weeklyRates);
        return response;
    }


    public List<CurrencyRatesResponse> toCurrencyRatesResponses(List<CurrencyRates> currencyRates, String rateKey, TimeSeries timeSeries) {
        List<CurrencyRatesResponse> list = new ArrayList<>();
        for (CurrencyRates rates : currencyRates) {
            list.add(toCurrencyRatesResponse(rates, rateKey, timeSeries));
        }
        return list;
    }



    private CurrencyRatesResponse toCurrencyRatesResponse(CurrencyRates currencyRates, String rateKey, TimeSeries timeSeries) {
        CurrencyRatesResponse currencyRatesResponse = new CurrencyRatesResponse();
        currencyRatesResponse.setDate(currencyRates.getDate());
        Double value = currencyRates.getRates().get(rateKey); // sirf ek key ka value nikalo
        currencyRatesResponse.setValue(value);
        switch (timeSeries) {
            case WEEK -> {
                // Sirf day name (MONDAY, TUESDAY ...)
                //String day = currencyRates.getDate().getDayOfWeek().toString().substring(0,3);
                String day = currencyRates.getDate().format(DateTimeFormatter.ofPattern("dd MMM"));
                currencyRatesResponse.setDay(day);
            }
            case MONTH -> {
                // Format: 25.Aug
                String formatted = currencyRates.getDate().format(DateTimeFormatter.ofPattern("dd MMM"));
                currencyRatesResponse.setDayMonth(formatted);
            }
//            case YEAR -> {
//// Sirf Month name (August)
//                String month = currencyRates.getDate().format(DateTimeFormatter.ofPattern("MMM YY"));
//                currencyRatesResponse.setMonth(month);
//            }
        }
        return currencyRatesResponse;
    }











    public List<CurrencyRatesResponse> toMonthlyAverageResponses(List<CurrencyRates> currencyRates, String rateKey) {
        // Group by Month and calculate average
        Map<YearMonth, Double> monthlyAverages = currencyRates.stream()
                .filter(r -> r.getRates().get(rateKey) != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(r -> r.getRates().get(rateKey))
                ));

        List<CurrencyRatesResponse> list = new ArrayList<>();
        monthlyAverages.forEach((yearMonth, avgValue) -> {
            CurrencyRatesResponse res = new CurrencyRatesResponse();
            res.setMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            res.setValue(avgValue);
            list.add(res);
        });
        return list;
    }







}