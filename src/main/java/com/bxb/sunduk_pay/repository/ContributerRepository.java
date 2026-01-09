package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Contributor entities.
 */
public interface ContributerRepository extends JpaRepository<Contributor,
        String> {

    /**
     * Check if a contributor exists by global pot ID and user UUID.
     * @param globalPotId
     * @param userUuid
     * @return
     */
    boolean existsByGlobalPotGlobalPotIdAndUserContributorUuid(
            String globalPotId,
            String userUuid
    );
}
