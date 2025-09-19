package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.Data;
/**
 * Request object for currency conversion and time series data.
 */

@Data
public class CurrencyRequest {
    String fromCurrency;
    String toCurrency;
    Double amount;
    TimeSeries timeSeries;
}