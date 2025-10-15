package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MasterWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for MasterWallet entity.
 * Extends MongoRepository to provide CRUD operations.
 */
public interface MasterWalletRepository
        extends JpaRepository<MasterWallet,String> {
   /**
    * Finds a MasterWallet by the associated user's UUID.
    * @param uuid the UUID of the user
    * @return an Optional containing the MasterWallet
    * if found, or empty if not found
    */
   Optional<MasterWallet> findByUserUuid(
            String uuid);

}
