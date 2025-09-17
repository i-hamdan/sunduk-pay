package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyResponse {
    private double exchangeRate;
    private double convertedAmount;
    private double conversionFee;
    private double finalAmount;
    private List<CurrencyRatesResponse> weeklyRates;
    private List<CurrencyRatesResponse> monthlyRates;
    private List<CurrencyRatesResponse> yearlyRates;

}