package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing Asset entities in the database.
 */
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Finds an asset by its symbol.
     *
     * @param symbol the symbol of the asset
     * @return an Optional containing the Asset if found, otherwise empty
     */
    Optional<Asset> findBySymbol(String symbol);
}
