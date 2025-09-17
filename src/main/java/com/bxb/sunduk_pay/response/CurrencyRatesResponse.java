package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

/**
 * Response payload for currency rate queries.
 * <p>
 * Contains the date, rate value, and formatted date fields
 * for easier display in the UI.
 * </p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyRatesResponse {

    /** The date of the currency rate. */
    private LocalDate date;

    /** The currency rate value. */
    private Double value;

    /** Day of the week (e.g., "Monday"). */
    private String day;

    /** Day and month (e.g., "12 Dec"). */
    private String dayMonth;

    /** Month name (e.g., "December"). */
    private String month;
}
