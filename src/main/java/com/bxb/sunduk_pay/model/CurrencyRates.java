package com.bxb.sunduk_pay.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents daily currency exchange rates stored in MongoDB.
 * <p>
 * Each document in the {@code currency_rates} collection contains
 * a specific date and a map of currency codes to their respective
 * exchange rates.
 * </p>
 */
@Document(collection = "currency_rates")
@Data
public class CurrencyRates {

    /**
     * The date for which the exchange rates apply.
     */
    private LocalDate date;

    /**
     * A map of currency codes to their exchange rates.
     * <p>
     * For example:
     * <pre>
     * {
     *   "USD": 83.25,
     *   "EUR": 89.10,
     *   "JPY": 0.56
     * }
     * </pre>
     * </p>
     */
    private Map<String, Double> rates = new HashMap<>();
}
