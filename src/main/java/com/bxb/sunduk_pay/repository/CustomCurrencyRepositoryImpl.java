package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.config.QueryConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom repository implementation for currency-related queries.
 * Provides methods to fetch specific currency rates
 * between given dates for specified currency pairs.
 */
@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomCurrencyRepositoryImpl implements CustomCurrencyRepository {

    /**
     * EntityManager for executing custom queries.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Configuration for query templates.
     */
    private final QueryConfig queryConfig;

    /**
     * Finds specific currency rates between given dates
     * for the specified currency pair.
     *
     * @param startDate    the start date for the rate search
     * @param endDate      the end date for the rate search
     * @param currencyPair the currency pair to search rates for
     * @return a list of maps containing date and rate information
     * @throws IllegalArgumentException if the currency pair is invalid
     */
    @Override
    public List<Map<String, Object>> findSpecificRate(
           final LocalDate startDate,
           final LocalDate endDate,
           final String currencyPair) {

         String findSpecificRate =  queryConfig.getModules()
                 .get("currency").get("findSpecificRate");
        String sql = findSpecificRate
                .replace("{column}",
                        currencyPair.toUpperCase());

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("rate", row[1]);
            list.add(map);
        }
        return list;
    }

}
