package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.CurrencyRates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CurrencyRepository extends JpaRepository<CurrencyRates, LocalDate>
        ,CustomCurrencyRepository {
}
