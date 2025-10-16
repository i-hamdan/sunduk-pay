package com.bxb.sunduk_pay.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomCurrencyRepository {
    List<Map<String, Object>> findSpecificRate(LocalDate startDate,
                                               LocalDate endDate,
                                               String currencyPair);
}
