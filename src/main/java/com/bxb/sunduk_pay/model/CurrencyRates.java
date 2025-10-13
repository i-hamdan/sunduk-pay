package com.bxb.sunduk_pay.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Model representing currency rates for a specific date.
 */
@Document(collection = "currency_rates")
@Data
public class CurrencyRates {
    /**
     * The date for which the currency rates are applicable.
     */
    private LocalDate date;
    /**
     * A map of currency codes to their respective exchange rates.
     */
    private Map<String, Double> rates = new HashMap<>();
}
