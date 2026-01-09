package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlobalPotMembersRepository
        extends JpaRepository<GlobalPotMembers, String> {

    Boolean existsByUserIdAndGlobalPotId(User userId, GlobalPot globalPotId);

    GlobalPotMembers findByUserIdAndGlobalPotId(User userId, GlobalPot globalPotId);

//    List<GlobalPotMembers> findByGlobalPotId(GlobalPot globalPotId);
//
//    List<GlobalPotMembers> findByUserId(User userId);
}
