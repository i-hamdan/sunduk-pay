package com.bxb.sunduk_pay.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for currency-related queries.
 */
public interface CustomCurrencyRepository {
    List<Map<String, Object>> findSpecificRate(
           final LocalDate startDate,
           final LocalDate endDate,
           final String currencyPair);
}
