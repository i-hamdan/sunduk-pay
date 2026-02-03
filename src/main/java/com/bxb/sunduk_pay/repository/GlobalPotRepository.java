package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.PotScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing GlobalPot entities in the database.
 */
public interface GlobalPotRepository extends JpaRepository<GlobalPot,
        String> {

    /**
     * Finds GlobalPots by their PotScope with pagination.
     *
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots matching the specified PotScope
     */
    Page<GlobalPot> findByPotScope(PotScope potScope, Pageable pageable);

    /**
     * Finds GlobalPots by their CaseCategory and PotScope with pagination.
     *
     * @param caseCategory the category of the case
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots matching the specified
     * CaseCategory and PotScope
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
    int getContributorsCountGlobalByPotId(
            @Param("globalPotId") String globalPotId);

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
    int getFollowersCountGlobalByPotId(
            @Param("globalPotId") String globalPotId);




    /**     * Finds all GlobalPots sorted by user interest metrics such as
     * visit count, total time spent, and last visited time.
     * @param userId the ID of the user
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots sorted by user interest
     */
    @Query(
            value = """
        SELECT gp
        FROM GlobalPot gp
        LEFT JOIN gp.interactions gpi
               ON gpi.uuid.id = :userId
        WHERE gp.potScope = :potScope
        ORDER BY
        (
            COALESCE(gpi.contributionCount, 0) * 3
          + COALESCE(gpi.messageCount, 0) * 2
          + COALESCE(gpi.visitCount, 0)
        ) DESC,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM GlobalPotInteraction g2
                WHERE g2.uuid.id = :userId
                  AND g2.caseCategory = gp.caseCategory
            )
            THEN 1
            ELSE 0
        END DESC,
        gpi.lastInteractedAt DESC
    """,
            countQuery = """
        SELECT COUNT(gp)
        FROM GlobalPot gp
        WHERE gp.potScope = :potScope
    """
    )
    Page<GlobalPot> findFeedSortedByBehavior(
            @Param("userId") String userId,
            @Param("potScope") PotScope potScope,
            Pageable pageable
    );


    /**     * Finds GlobalPots by CaseCategory sorted by user interest metrics
     * such as visit count, total time spent, and last visited time.
     * @param userId the ID of the user
     * @param caseCategory the category of the case
     * @param potScope the scope of the pot
     * @param pageable pagination information
     * @return a page of GlobalPots matching the specified CaseCategory
     * and sorted by user interest
     */
    @Query(
            value = """
        SELECT gp
        FROM GlobalPot gp
        LEFT JOIN gp.interactions gpi
               ON gpi.uuid.id = :userId
        WHERE gp.potScope = :potScope
          AND gp.caseCategory = :caseCategory
        ORDER BY
        (
            COALESCE(gpi.contributionCount, 0) * 3
          + COALESCE(gpi.messageCount, 0) * 2
          + COALESCE(gpi.visitCount, 0)
        ) DESC,
        gpi.lastInteractedAt DESC
    """,
            countQuery = """
        SELECT COUNT(gp)
        FROM GlobalPot gp
        WHERE gp.potScope = :potScope
          AND gp.caseCategory = :caseCategory
    """
    )
    Page<GlobalPot> findCategoryFeedSortedByBehavior(
            @Param("userId") String userId,
            @Param("caseCategory") CaseCategory caseCategory,
            @Param("potScope") PotScope potScope,
            Pageable pageable
    );

}
