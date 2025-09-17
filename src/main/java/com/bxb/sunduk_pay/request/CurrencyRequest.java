package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.Data;

/**
 * Represents a request payload for currency conversion or rate queries.
 * <p>
 * Includes the source currency, target currency, the amount to convert,
 * and an optional time series parameter for historical rate queries.
 * </p>
 */
@Data
public class CurrencyRequest {

    /**
     * The source currency code (e.g., "USD") from which the amount will be converted.
     */
    private String fromCurrency;

    /**
     * The target currency code (e.g., "EUR") to which the amount will be converted.
     */
    private String toCurrency;

    /**
     * The amount to convert from the source currency to the target currency.
     */
    private Double amount;

    /**
     * Optional parameter specifying the time series for historical currency rates.
     */
    private TimeSeries timeSeries;
}
