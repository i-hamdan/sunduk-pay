package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MainWallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on
 * {@link MainWallet} documents stored in MongoDB.
 * <p>
 * Provides methods to access main wallet data by user identifiers.
 * </p>
 */
@Repository
public interface MainWalletRepository extends MongoRepository<MainWallet, String> {

    /**
     * Finds the main wallet associated with a given user UUID.
     *
     * @param uuid the unique identifier of the user
     * @return an {@link Optional} containing the {@link MainWallet}
     *         if found, or an empty Optional if no wallet exists
     *         for the given user
     */
    @SuppressWarnings("checkstyle:MethodName")
    Optional<MainWallet> findByUser_Uuid(final String uuid);
}
