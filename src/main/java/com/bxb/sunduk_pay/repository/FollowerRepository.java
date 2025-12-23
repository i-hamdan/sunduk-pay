package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, String> {

    // check already followed
    boolean existsByFollowerUserAndGlobalPot(User followerUser, GlobalPot globalPot);
}
