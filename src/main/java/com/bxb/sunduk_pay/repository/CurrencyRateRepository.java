package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.CurrencyRates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface CurrencyRateRepository extends MongoRepository<CurrencyRates, String> {
//    @Query("{ 'date': { $gte: ?1 }, 'rates.?0': { $exists: true } }")
//    List<CurrencyRates> findRatesForLastYear(String currencyPair, LocalDateTime oneYearAgo);

    @Query(
            value = "{ 'date': { $gte: ?0 } }",
            fields = "{ 'date': 1, 'rates.?1': 1 }"
    )
    List<CurrencyRates> findSpecificRate(LocalDate timePeriod, String rateKey);
}