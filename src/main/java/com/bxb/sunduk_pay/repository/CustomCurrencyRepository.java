package com.bxb.sunduk_pay.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for currency-related queries.
 */
public interface CustomCurrencyRepository {

    /**
     * Finds specific currency rates within a date
     * range for a given currency pair.
     *
     * @param startDate    the start date of the range
     * @param endDate      the end date of the range
     * @param currencyPair the currency pair to filter by
     * @return a list of maps containing the currency
     * rates and related information
     */
    List<Map<String, Object>> findSpecificRate(
            LocalDate startDate,
            LocalDate endDate,
            String currencyPair);
}
