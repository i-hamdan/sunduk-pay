package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing PortfolioModel entities in the database.
 */
public interface PortfolioModelRepository extends JpaRepository<
        PortfolioModel, Long> {
    Optional<PortfolioModel> findByName(String name);
}
