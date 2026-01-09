package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPotBlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing GlobalPotBlockedUser entities.
 */
public interface GlobalPotBlockedUserRepository
        extends JpaRepository<GlobalPotBlockedUser, String> {


    /**
     * Finds a GlobalPotBlockedUser by global pot ID and user UUID.
     *
     * @param globalPotId the ID of the global pot
     * @param userUuid    the UUID of the user
     * @return an Optional containing the found GlobalPotBlockedUser,
     * or empty if not found
     */
    Optional<GlobalPotBlockedUserRepository>
    findByGlobalPotGlobalPotIdAndUserUuid(String globalPotId, String userUuid
    );

    /**
     * Checks if a GlobalPotBlockedUser exists by global pot ID and user UUID.
     *
     * @param globalPotId the ID of the global pot
     * @param userUuid    the UUID of the user
     * @return true if a GlobalPotBlockedUser exists, false otherwise
     */
    boolean existsByGlobalPotGlobalPotIdAndUserUuid(
            String globalPotId,
            String userUuid
    );

}
