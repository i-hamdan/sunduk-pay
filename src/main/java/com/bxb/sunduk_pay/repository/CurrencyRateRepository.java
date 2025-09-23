package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.CurrencyRates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for accessing
 * currency rates stored in MongoDB.
 * Extends MongoRepository to provide
 * CRUD operations and custom queries.
 */
@Repository

public interface CurrencyRateRepository
        extends MongoRepository<CurrencyRates, String> {
    /**
     * Finds currency rates within a specified date range.
     * Finds currency rates within a specified date
     * range and retrieves only the specified rate key.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate   the end date of the range (inclusive)
     * @param rateKey   the specific currency rate key to retrieve
     *                 (e.g., "USD", "EUR")
     * @return a list of CurrencyRates objects
     * containing only the date and specified rate
     */
    @Query(
            value = "{ 'date': { $gte: ?0, $lte: ?1 } }",
            fields = "{ 'date': 1, 'rates.?2': 1 }"
    )
    List<CurrencyRates> findSpecificRate(
            LocalDate startDate,
            LocalDate endDate,
            String rateKey);
}










