package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.CurrencyRates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for accessing and querying
 * {@link CurrencyRates} documents in the MongoDB
 * {@code currency_rates} collection.
 *
 * <p>
 * Provides custom queries to retrieve exchange rates
 * for specific currencies over a given date range.
 * </p>
 */
@Repository
public interface CurrencyRateRepository
        extends MongoRepository<CurrencyRates, String> {

    /**
     * Finds the exchange rate for a specific currency between
     * the given start and end dates.
     *
     * <p>
     * This query projects only the {@code date} field and
     * the rate value for the provided currency key to reduce
     * unnecessary data transfer.
     * </p>
     *
     * @param startDate the start date (inclusive)
     *                  for filtering currency rates
     * @param endDate   the end date (inclusive)
     *                  for filtering currency rates
     * @param rateKey   the currency code (e.g., "USD", "EUR")
     *                  whose rate should be retrieved
     * @return a list of {@link CurrencyRates} objects containing
     * the date and the specified currency rate
     */
    @Query(
            value = "{ 'date': { $gte: ?0, $lte: ?1 } }",
            fields = "{ 'date': 1, 'rates.?2': 1 }"
    )
    List<CurrencyRates> findSpecificRate(
            LocalDate startDate,
            LocalDate endDate,
            String rateKey
    );
}
