package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvestmentDailyHistoryRepository
        extends JpaRepository<InvestmentDailyHistory, Long> {

    /**
     * Finds investment daily history records for the given investment IDs
     * within the specified date range, ordered by snapshot date ascending.
     *
     * @param uuid         user UUID
     * @param startDate     start date of the range
     * @param endDate       end date of the range
     * @return list of InvestmentDailyHistory records
     */
    List<InvestmentDailyHistory> findByUserUuidAndSnapshotDateBetween(
            String uuid,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Finds investment daily history records for a specific investment ID.
     *
     * @param investmentId the investment ID
     * @return list of InvestmentDailyHistory records
     */
    List<InvestmentDailyHistory> findByInvestmentInvestmentId(
            String investmentId);

    /**
     * Fetch latest snapshot entry for a given investment.
     * (most recent snapshotDate)
     * @param investment the investment entity
     * @return the latest InvestmentDailyHistory record
     */
    InvestmentDailyHistory findTopByInvestmentOrderBySnapshotDateDesc(
            Investment investment
    );

}
