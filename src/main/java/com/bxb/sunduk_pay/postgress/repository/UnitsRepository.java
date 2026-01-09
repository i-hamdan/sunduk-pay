package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing Units entities.
 */
public interface UnitsRepository extends JpaRepository<Units, Long> {


    /** Find units by portfolio model and dates before
     *  or on the specified date.
     *
     * @param model the portfolio model
     * @param date  the date to compare
     * @return list of Units entities matching the criteria
     */
    @Query("""
            SELECT u FROM Units u
            WHERE u.portfolioModel = :model
            AND u.date <= :date
            ORDER BY u.date DESC
            """)
    List<Units> findLatestBeforeOrOnDate(PortfolioModel model, LocalDate date);


    /** Find units by portfolio model and dates after the specified date.
     *
     * @param model the portfolio model
     * @param date  the date to compare
     * @return list of Units entities matching the criteria
     */
    @Query("""
    SELECT u
    FROM Units u
    WHERE u.portfolioModel = :model
    AND u.date > :date
    ORDER BY u.date ASC
    """)
    List<Units> findNextAfterDate(PortfolioModel model, LocalDate date);

    /** Find units by portfolio model ID and exact date.
     *
     * @param modelId the ID of the portfolio model
     * @param date    the exact date to search for
     * @return the Units entity matching the criteria
     */
    @Query("""
        SELECT u FROM Units u
        WHERE u.portfolioModel.id = :modelId
        AND u.date = :date
        """)
    Units findByPortfolioModelAndDate(Long modelId, LocalDate date);


    /**     * Check if units exist for a given date.
     *
     * @param date the date to check
     * @return true if units exist for the date, false otherwise
     */
    boolean existsByDate(LocalDate date);
}
