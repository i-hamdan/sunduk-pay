package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPotBlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalPotBlockedUserRepository extends JpaRepository<GlobalPotBlockedUser,String> {


    Optional<GlobalPotBlockedUserRepository> findByGlobalPotGlobalPotIdAndUserUuid(
            String globalPotId,
            String userUuid
    );

    boolean existsByGlobalPotGlobalPotIdAndUserUuid(
            String globalPotId,
            String userUuid
    );

}
