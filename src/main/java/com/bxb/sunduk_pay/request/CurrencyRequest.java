package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.Data;

@Data
public class CurrencyRequest {
    String fromCurrency;
    String toCurrency;
    Double amount;
    TimeSeries timeSeries;
}