package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributerRepository extends JpaRepository<Contributor, String> {

    /**
     * Check if a contributor exists by global pot ID and user UUID.
     * @param globalPotId
     * @param userUuid
     * @return
     */
    boolean existsByGlobalPot_GlobalPotIdAndUserContributor_Uuid(
            String globalPotId,
            String userUuid
    );
}
