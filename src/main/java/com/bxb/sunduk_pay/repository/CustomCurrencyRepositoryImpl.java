package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.config.QueryConfig;
import com.bxb.sunduk_pay.util.CurrencyPair;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
@Log4j2
public class CustomCurrencyRepositoryImpl implements CustomCurrencyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private QueryConfig queryConfig;

    @Override
    public List<Map<String, Object>> findSpecificRate(LocalDate startDate,
                                                      LocalDate endDate,
                                                      String currencyPair) {
        // Validate enum
        try {
            CurrencyPair.valueOf(currencyPair);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency pair: " + currencyPair);
        }

         String findSpecificRate =  queryConfig.getModules().get("currency").get("findSpecificRate");
        String sql = findSpecificRate.replace("{column}", currencyPair.toUpperCase());

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
