package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
                String day = currencyRates.getDate().getDayOfWeek().toString().substring(0,3);
                currencyRatesResponse.setDay(day);
            }
            case MONTH -> {
                // Format: 25.Aug
                String formatted = currencyRates.getDate().format(DateTimeFormatter.ofPattern("dd.MMM"));
                currencyRatesResponse.setDayMonth(formatted);
            }
            case YEAR -> {
                // Sirf Month name (August)
                String month = currencyRates.getDate().format(DateTimeFormatter.ofPattern("MMMM"));
                currencyRatesResponse.setMonth(month);
            }
        }
        return currencyRatesResponse;
    }
}