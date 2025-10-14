package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * Repository interface for User entity.
 * Extends MongoRepository to provide CRUD
 * operations on User documents in MongoDB.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by email who is not marked as deleted.
     *
     * @param email the email of the user
     * @return an Optional containing the User if found,
     * or empty if not found
     */
    Optional<User> findByEmailAndIsDeletedFalse(
            String email);


}
