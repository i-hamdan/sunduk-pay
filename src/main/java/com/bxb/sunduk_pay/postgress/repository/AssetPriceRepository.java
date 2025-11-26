package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.Asset;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
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

/**    * Finds the closest AssetPrice to a given date for a specific asset.
     *
     * @param assetId the ID of the asset
     * @param date    the target date and time
     * @return a list of AssetPrices ordered by proximity to the target date
     */
    @Query(value = """
        SELECT *
        FROM asset_prices ap
        WHERE ap.stock_id = :assetId
        ORDER BY ABS(EXTRACT(EPOCH FROM (ap.effective_at - :date)))
        LIMIT 1
        """, nativeQuery = true)
    List<AssetPrice> findClosestPrice(Long assetId, LocalDateTime date);

    @Query(value = """
        SELECT *
        FROM asset_prices ap
        WHERE ap.stock_id = :assetId
        ORDER BY ap.effective_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<AssetPrice> findLatestByAsset(Long assetId);
}
