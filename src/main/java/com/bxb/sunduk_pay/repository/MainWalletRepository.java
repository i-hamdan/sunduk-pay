package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MainWallet;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing MainWallet entities in MongoDB.
 */
public interface MainWalletRepository
        extends JpaRepository<MainWallet, String> {

 /** Finds a MainWallet by the associated user's UUID.
  * @param uuid The UUID of the user.
  * @return An Optional containing the MainWallet if found,
  * or empty if not found.
  */
 Optional<MainWallet> findByUserUuid(
         String uuid);

}
