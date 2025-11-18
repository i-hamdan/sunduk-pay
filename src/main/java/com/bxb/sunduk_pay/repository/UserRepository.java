package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository interface for User entity.
 * Extends MongoRepository to provide CRUD
 * operations on User documents in MongoDB.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Finds a user by email who is not marked as deleted.
     *
     * @param email the email of the user
     * @return an Optional containing the User if found,
     * or empty if not found
     */
    Optional<User> findByEmailAndIsDeletedFalse(
            String email);

    /**
     * Finds a user by phone number hash.
     *
     * @param phoneNumberHash the hash of the user's phone number
     * @return an Optional containing the User if found,
     * or empty if not found
     */
    Optional<User> findByPhoneNumberHash(String phoneNumberHash);
/**
     * Finds a user by phone number.
     *
     * @param phoneNumber the user's phone number
     * @return an Optional containing the User if found,
     * or empty if not found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

}
