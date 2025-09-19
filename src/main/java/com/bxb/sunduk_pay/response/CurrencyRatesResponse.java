package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

/**
 * Response object for currency rates.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyRatesResponse {
    private LocalDate date;
    private Double value;
    private String day;
    private String dayMonth;
    private String month;
}