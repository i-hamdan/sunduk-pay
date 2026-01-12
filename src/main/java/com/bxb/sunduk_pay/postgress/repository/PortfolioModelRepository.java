package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing PortfolioModel entities in the database.
 */
public interface PortfolioModelRepository extends JpaRepository<
        PortfolioModel, Long> {
    /**
     * Finds a PortfolioModel entity by its name.
     *
     * @param name the name of the PortfolioModel
     * @return an Optional containing the found PortfolioModel,
     *         or empty if not found
     */
    Optional<PortfolioModel> findByName(String name);
}
