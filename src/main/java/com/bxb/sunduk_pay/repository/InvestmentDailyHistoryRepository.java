package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InvestmentDailyHistoryRepository
        extends JpaRepository<InvestmentDailyHistory, Long> {

}
