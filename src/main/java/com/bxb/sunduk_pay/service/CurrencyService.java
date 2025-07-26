package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.response.CurrencyResponse;

public interface CurrencyService {
    CurrencyResponse convertCurrency(String fromCurrency, String toCurrency, Double amount);
}
