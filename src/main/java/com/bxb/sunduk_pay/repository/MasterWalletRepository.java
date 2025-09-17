package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MasterWallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link MasterWallet} documents
 * stored in MongoDB.
 * <p>
 * Provides methods to retrieve the master wallet associated with a specific user.
 * </p>
 */
public interface MasterWalletRepository extends MongoRepository<MasterWallet, String> {

    /**
     * Finds the master wallet associated with a given user UUID.
     *
     * @param uuid the unique identifier of the user
     * @return an {@link Optional} containing the {@link MasterWallet} if found,
     *         or an empty Optional if no wallet exists for the given user
     */
    Optional<MasterWallet> findByUser_Uuid(String uuid);
}
