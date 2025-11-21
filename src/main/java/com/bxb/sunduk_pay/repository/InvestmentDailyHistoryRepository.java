package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvestmentDailyHistoryRepository
        extends JpaRepository<InvestmentDailyHistory, Long> {

    List<InvestmentDailyHistory> findByInvestmentOrderBySnapshotDateDesc(Investment investment);

    boolean existsByInvestmentAndSnapshotDate(Investment investment, LocalDate date);
}
