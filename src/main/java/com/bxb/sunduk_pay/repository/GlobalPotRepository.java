package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GlobalPotRepository extends JpaRepository<GlobalPot, String> {


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
