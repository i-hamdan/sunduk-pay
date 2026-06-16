package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing GlobalWallet entities in the database.
 */
public interface GlobalWalletRepository
        extends JpaRepository<GlobalWallet, String> {
    /**
     * Find a GlobalWallet by its globalWalletId.
     *
     * @param globalPotId the ID of the global wallet
     * @return an Optional containing the found GlobalWallet,
     * or empty if not found
     */
    Optional<GlobalWallet> findByGlobalPotGlobalPotId(String globalPotId);
}
