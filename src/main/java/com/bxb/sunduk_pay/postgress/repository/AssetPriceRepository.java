package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.Asset;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing AssetPrice entities in the database.
 */
public interface AssetPriceRepository extends JpaRepository<AssetPrice, Long> {

    /**
     * Finds an AssetPrice by the associated Asset and the effective date and time.
     *
     * @param asset       the Asset entity
     * @param effectiveAt the effective date and time
     * @return an Optional containing the AssetPrice if found, otherwise empty
     */
    Optional<AssetPrice> findByAssetAndEffectiveAt(Asset asset,
                                                   LocalDateTime effectiveAt);

    //This method allows Spring Data JPA to generate the SQL query needed to
    // find an existing price record for the given asset and date.
}
