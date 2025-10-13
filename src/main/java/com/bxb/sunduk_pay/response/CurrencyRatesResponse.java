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
/** Date of the currency rate. */
    private LocalDate date;
   /** Value of the currency rate. */
    private Double value;
  /** Day of the currency rate. */
    private String day;
  /** Day and month of the currency rate. */
    private String dayMonth;
  /** Month of the currency rate. */
    private String month;
}
