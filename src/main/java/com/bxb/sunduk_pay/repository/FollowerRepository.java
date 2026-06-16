package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Follower entities.
 */
public interface FollowerRepository extends JpaRepository<Follower, String> {

    /**
     * Checks if a follower relationship exists
     * between a user and a global pot.
     *
     * @param followerUser the user who is following
     * @param globalPot    the global pot being followed
     * @return true if the follower relationship exists, false otherwise
     */
    // check already followed
    boolean existsByFollowerUserAndGlobalPot(User followerUser,
                                             GlobalPot globalPot);
}
