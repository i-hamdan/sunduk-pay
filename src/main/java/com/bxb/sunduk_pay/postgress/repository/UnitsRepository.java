package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UnitsRepository extends JpaRepository<Units, Long> {


// find exact date or before date of units
    @Query("""
            SELECT u FROM Units u 
            WHERE u.portfolioModel = :model 
            AND u.date <= :date 
            ORDER BY u.date DESC
            """)
    List<Units> findLatestBeforeOrOnDate(PortfolioModel model, LocalDate date);


// find next date  units
    @Query("""
    SELECT u
    FROM Units u
    WHERE u.portfolioModel = :model
    AND u.date > :date
    ORDER BY u.date ASC
    """)
    List<Units> findNextAfterDate(PortfolioModel model, LocalDate date);


    boolean existsByDate(LocalDate date);
}
