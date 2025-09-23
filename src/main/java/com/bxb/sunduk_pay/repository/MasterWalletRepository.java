package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MasterWallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
/**
 * Repository interface for MasterWallet entity.
 * Extends MongoRepository to provide CRUD operations.
 */
public interface MasterWalletRepository extends MongoRepository<MasterWallet,String> {
   /**
    * Finds a MasterWallet by the associated user's UUID.
    * @param uuid the UUID of the user
    */
// CHECKSTYLE:OFF
   Optional<MasterWallet> findByUser_Uuid(
            String uuid);
   // CHECKSTYLE:ON

}
