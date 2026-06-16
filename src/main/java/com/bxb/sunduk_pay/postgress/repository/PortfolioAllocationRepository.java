package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioAllocation;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing PortfolioAllocation
 *      entities in the database.
 */
public interface PortfolioAllocationRepository extends JpaRepository<
        PortfolioAllocation, Long> {

    /**
     * Finds all PortfolioAllocation entities
     * associated with a given PortfolioModel.
     *
     * @param model the PortfolioModel to search by
     * @return a list of PortfolioAllocation entities
     */
    List<PortfolioAllocation> findByPortfolioModel(
            PortfolioModel model);
}
