package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlobalPotMembersRepository
        extends JpaRepository<GlobalPotMembers, String> {

    Boolean existsByUserAndGlobalPot(User userId, GlobalPot globalPotId);

    Optional<GlobalPotMembers> findByUserAndGlobalPot(User userId, GlobalPot globalPotId);

//    List<GlobalPotMembers> findByGlobalPotId(GlobalPot globalPotId);
//
//    List<GlobalPotMembers> findByUserId(User userId);
}
