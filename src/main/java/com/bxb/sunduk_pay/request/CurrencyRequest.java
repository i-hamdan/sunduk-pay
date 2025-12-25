package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.*;

/**
 * Request object for currency conversion and time series data.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyRequest {
    /**
     * The currency code to convert from (e.g., "USD").
     */
    private String fromCurrency;
    /**
     * The currency code to convert to (e.g., "EUR").
     */
    private String toCurrency;
    /**
     * The amount to be converted.
     */
    private Double amount;
    /**
     * The time series option for historical data
     * (e.g., DAILY, WEEKLY, MONTHLY).
     */
    private TimeSeries timeSeries;
}
