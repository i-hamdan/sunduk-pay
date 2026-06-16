package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing AnonymousUser entities.
 */
@Repository
public interface AnonymousUserRepository
        extends JpaRepository<AnonymousUser, String> {

    /**
     * Finds an AnonymousUser by the associated GlobalPot ID and User UUID.
     *
     * @param globalPotId the ID of the GlobalPot
     * @param userId      the UUID of the User
     * @return an Optional containing the found AnonymousUser,
     * or empty if not found
     */
    Optional<AnonymousUser>
    findByGlobalPotGlobalPotIdAndUserUuid(
            String globalPotId,
            String userId
    );
}
