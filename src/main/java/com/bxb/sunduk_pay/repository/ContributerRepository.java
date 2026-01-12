package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for Contributor entity.
 */
public interface ContributerRepository
        extends JpaRepository<Contributor, String> {

    /**
     * Check if a contributor exists by global pot ID and user UUID.
     * @param globalPotId
     * @param userUuid
     * @return true if exists, false otherwise
     */
    boolean existsByGlobalPotGlobalPotIdAndUserContributorUuid(
            String globalPotId,
            String userUuid
    );
}
