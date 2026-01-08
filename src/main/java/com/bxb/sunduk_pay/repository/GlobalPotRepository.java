package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.PotScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
/**
 * Repository interface for managing GlobalPot entities in the database.
 */
public interface GlobalPotRepository extends JpaRepository<GlobalPot, String> {

    /**
     * Finds GlobalPots by their PotScope with pagination.
     *
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots matching the specified PotScope
     */
    Page<GlobalPot> findByPotScope(PotScope potScope,Pageable pageable);

    /**
     * Finds GlobalPots by their CaseCategory and PotScope with pagination.
     *
     * @param caseCategory the category of the case
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots matching the specified CaseCategory and PotScope
     */
    Page<GlobalPot> findByCaseCategoryAndPotScope(
            CaseCategory caseCategory,
            PotScope potScope,
            Pageable pageable
    );



    /**
     * Retrieves the count of contributors for a given Global Pot by its ID.
     *
     * @param globalPotId the ID of the Global Pot
     * @return the number of contributors associated with the Global Pot
     */
    @Query("""
            select Count(c)
            from Contributor c
            where c.globalPot.globalPotId = :globalPotId
            """
    )
    int getContributorsCountGlobalByPotId(@Param("globalPotId") String globalPotId);

    /**
     * Retrieves the count of followers for a given Global Pot by its ID.
     *
     * @param globalPotId the ID of the Global Pot
     * @return the number of followers associated with the Global Pot
     */
    @Query(
            """
            select Count(f)
            from Follower f
            where f.globalPot.globalPotId = :globalPotId
           """
    )
    int getFollowersCountGlobalByPotId(@Param("globalPotId") String globalPotId);
}
