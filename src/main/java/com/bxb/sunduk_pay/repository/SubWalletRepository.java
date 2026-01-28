package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.SubWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing SubWallet
 * entities in the database.
 */
@Repository
public interface SubWalletRepository extends
        JpaRepository<SubWallet, String> {

    /** Find a non-deleted sub-wallet by its name
     * (case-insensitive) and associated main wallet ID.
     * @param name the name of the sub-wallet
     * @param mainWalletId the ID of the associated main wallet
     * @return an Optional containing the found SubWallet,
     * or empty if not found
     */
    @Query("""
    SELECT s FROM SubWallet s
    WHERE LOWER(s.subWalletName) = LOWER(:name)
    AND s.mainWallet.mainWalletId = :mainWalletId
    AND s.isDeleted = false
""")
    Optional<SubWallet> findActiveByNameAndMainWallet(
            @Param("name") String name,
            @Param("mainWalletId") String mainWalletId
    );


    /** Find a sub-wallet by its ID and associated main wallet ID,
     * only if it is not marked as deleted.
     * @param subWalletId the ID of the sub-wallet
     * @param mainWalletId the ID of the associated main wallet
     * @return an Optional containing the found SubWallet,
     * or empty if not found
     */
    Optional<SubWallet>
    findBySubWalletIdAndMainWalletMainWalletIdAndIsDeletedFalse(
            String subWalletId, String mainWalletId);

    /** Find all non-deleted sub-wallets associated
     * with a given main wallet ID.
     * @param mainWalletId the ID of the main wallet
     * @return a list of non-deleted SubWallets
     */
    List<SubWallet>
    findAllByMainWalletMainWalletIdAndIsDeletedFalse(
            String mainWalletId);

    @Query("SELECT COUNT(sw) FROM SubWallet sw WHERE sw.mainWallet.mainWalletId = :mainWalletId AND sw.isDeleted = false")
    long getSubWalletCountByMainWalletMainWalletId(String mainWalletId);
}
