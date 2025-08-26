package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;

import java.util.List;
import java.util.Map;

public interface CurrencyMapper {
    public CurrencyResponse currencyResponse(double exchangeRate, double converted, double fee, double finalAmount , List<CurrencyRatesResponse> yearlyRates, List<CurrencyRatesResponse> monthlyRates, List<CurrencyRatesResponse> weeklyRates);
    List<CurrencyRatesResponse> toCurrencyRatesResponses(List<CurrencyRates> currencyRates, String rateKey, TimeSeries timeSeries);
}