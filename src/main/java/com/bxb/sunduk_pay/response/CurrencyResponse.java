package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response object for currency conversion and exchange rate information.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CurrencyResponse {
    /** Exchange rate from source currency to target currency. */
    private double exchangeRate;
    /** The original amount before conversion. */
    private double convertedAmount;
    /** The fee applied for the conversion. */
    private double conversionFee;
    /** The final amount after conversion and fees. */
    private String finalAmount;
    /** List of daily currency rates.*/
    private List<CurrencyRatesResponse> weeklyRates;
    /** List of monthly currency rates.*/
    private List<CurrencyRatesResponse> monthlyRates;
/** List of yearly currency rates.*/
    private List<CurrencyRatesResponse> yearlyRates;
}
