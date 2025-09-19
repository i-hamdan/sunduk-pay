package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyResponse;

/**
 * Service interface for currency conversion operations.
 */
public interface CurrencyService {
    /**
     * Converts currency based on the provided request details.
     *
     * @param currencyRequest the request containing currency conversion details
     * @return a response containing the result of the currency conversion
     */
    CurrencyResponse convertCurrency(CurrencyRequest currencyRequest);
}