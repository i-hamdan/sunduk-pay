package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.SubWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing SubWallet
 * entities in the database.
 */
public interface SubWalletRepository extends
        JpaRepository<SubWallet, String> {
    Optional<SubWallet> findBySubWalletNameIgnoreCaseAndIsDeletedFalse(String subWalletName);
    Optional<SubWallet> findBySubWalletIdAndMainWallet_MainWalletIdAndIsDeletedFalse(
            String subWalletId, String mainWalletId);
    List<SubWallet> findAllByMainWallet_MainWalletIdAndIsDeletedFalse(String mainWalletId);
}
