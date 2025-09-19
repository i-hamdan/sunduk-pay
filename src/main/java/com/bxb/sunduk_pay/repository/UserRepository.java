package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on
 * {@link User} documents stored in MongoDB.
 *
 * <p>
 * Provides custom query methods to fetch users by email or UUID.
 * </p>
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by email address, ensuring that the user
     * is not marked as deleted.
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the {@link User}
     *         if found and not deleted, otherwise an empty Optional
     */
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    /**
     * Finds a user by their unique UUID.
     *
     * @param uuid the unique identifier of the user
     * @return the {@link User} with the specified UUID,
     *         or {@code null} if not found
     */
    User findByUuid(String uuid);
}
