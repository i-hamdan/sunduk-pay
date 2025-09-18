package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyResponse;

/**
 * Service interface for handling currency conversion operations.
 */
public interface CurrencyService {

    /**
     * Converts currency based on the provided request.
     *
     * @param currencyRequest the currency conversion request
     * @return the conversion result as a CurrencyResponse
     */
    CurrencyResponse convertCurrency(CurrencyRequest currencyRequest);
}
