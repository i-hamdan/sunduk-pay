package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.Asset;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing AssetPrice entities in the database.
 */
public interface AssetPriceRepository extends JpaRepository<AssetPrice, Long> {

    /**
     * Finds an AssetPrice by the associated Asset and the
     * effective date and time.
     *
     * @param asset       the Asset entity
     * @param effectiveAt the effective date and time
     * @return an Optional containing the AssetPrice if found, ot
     * herwise empty
     */
    Optional<AssetPrice> findByAssetAndEffectiveAt(Asset asset,
                                                   LocalDateTime effectiveAt);


/**
  * Finds the most recent AssetPrice before or on a
 * given date for a specific Asset.

     * @param assetId the ID of the Asset
     * @param date    the specific date
     * @return the most recent AssetPrice before or on the given date
     */
    @Query(value = """
        SELECT *
        FROM asset_prices ap
        WHERE ap.stock_id = :assetId
          AND CAST(ap.effective_at AS DATE) > :date
        ORDER BY ap.effective_at ASC
        LIMIT 1
    """, nativeQuery = true)
    AssetPrice findNextPrice(
            @Param("assetId") Long assetId,
            @Param("date") LocalDate date);


    /**     * Finds the earliest AssetPrice for a given Asset.
     *
     * @param asset the Asset entity
     * @return the earliest AssetPrice for the given asset
     */
    AssetPrice findTopByAssetOrderByEffectiveAtAsc(Asset asset);

    /**
     * Retrieves all unique dates on which asset prices were effective.
     *
     * @return a list of unique dates
     */
    @Query("SELECT DISTINCT CAST(a.effectiveAt AS date) "
            + "FROM AssetPrice a ORDER BY CAST(a.effectiveAt AS date)")
    List<java.sql.Date> findAllUniqueDates();



    /**
     * Finds an AssetPrice by the associated Asset and a specific date.
     *
     * @param asset the Asset entity
     * @param date  the specific date
     * @return the AssetPrice for the given asset and date
     */
    @Query(
            "SELECT a FROM AssetPrice a WHERE DATE(a.effectiveAt)"
                    + "= :date AND a.asset = :asset")
    AssetPrice findByAssetAndDate(Asset asset, LocalDate date);


}
