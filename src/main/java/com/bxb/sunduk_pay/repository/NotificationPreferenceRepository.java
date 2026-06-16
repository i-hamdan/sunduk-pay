package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing NotificationPreference entities.
 * Provides methods to perform CRUD operations and custom queries
 * related to user notification preferences.
 */
@Repository
public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference,String> {
    /**
     * Finds the notification preference for a user by their UUID.
     * @param userUuid the UUID of the user
     * @return an Optional containing the NotificationPreference if found,
     * or empty if not found
     */
    Optional<NotificationPreference> findByUserUuid(String userUuid);

    /**
     * Checks if a notification preference exists for a given user.
     * @param user the User entity to check for
     * @return true if a notification preference exists for the user,
     * false otherwise
     */
    boolean existsByUser(User user);
}
