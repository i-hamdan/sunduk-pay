package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.response.CurrencyResponse;

public interface CurrencyMapper {
    public CurrencyResponse currencyResponse(double exchangeRate, double converted, double fee, double finalAmount);
}
