package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MainWallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing MainWallet entities in MongoDB.
 */
@Repository
public interface MainWalletRepository
        extends MongoRepository<MainWallet, String> {

 /** Finds a MainWallet by the associated user's UUID.
  * @param uuid The UUID of the user.
  * @return An Optional containing the MainWallet if found,
  * or empty if not found.
  */
// CHECKSTYLE:OFF
 Optional<MainWallet> findByUser_Uuid(
         String uuid);
// CHECKSTYLE:ON

}
