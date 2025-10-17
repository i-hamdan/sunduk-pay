package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.SubWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing SubWallet
 * entities in the database.
 */
@Repository
public interface SubWalletRepository extends
        JpaRepository<SubWallet, String> {
    /** Find a sub-wallet by its name, ignoring case,
     *  only if it is not marked as deleted.
     * @param subWalletName the name of the sub-wallet
     * @return an Optional containing the found SubWallet,
     * or empty if not found
     */
    Optional<SubWallet>
    findBySubWalletNameIgnoreCaseAndIsDeletedFalse(
            String subWalletName);

    /** Find a sub-wallet by its ID and associated main wallet ID,
     * only if it is not marked as deleted.
     * @param subWalletId the ID of the sub-wallet
     * @param mainWalletId the ID of the associated main wallet
     * @return an Optional containing the found SubWallet,
     * or empty if not found
     */
    Optional<SubWallet>
    findBySubWalletIdAndMainWallet_MainWalletIdAndIsDeletedFalse(
            String subWalletId, String mainWalletId);

    /** Find all non-deleted sub-wallets associated
     * with a given main wallet ID.
     * @param mainWalletId the ID of the main wallet
     * @return a list of non-deleted SubWallets
     */
    List<SubWallet>
    findAllByMainWallet_MainWalletIdAndIsDeletedFalse(
            String mainWalletId);
}
