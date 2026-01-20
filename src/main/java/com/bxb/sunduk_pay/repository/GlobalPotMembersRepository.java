package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.UserRoles;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlobalPotMembersRepository
        extends JpaRepository<GlobalPotMembers, String> {

    /**
     * Check if GlobalPotMembers exists by User and GlobalPot.
     * @param userId
     * @param globalPotId
     * @return Boolean.
     */
    Boolean existsByUserAndGlobalPot(User userId, GlobalPot globalPotId);

    /**
     * Find GlobalPotMembers by User and GlobalPot.
     * @param userId
     * @param globalPotId
     * @return Optional of GlobalPotMembers.
     */
    Optional<GlobalPotMembers> findByUserAndGlobalPot(
            User userId, GlobalPot globalPotId);



    /**     * Find GlobalPotMembers by GlobalPot ID.
     * @param globalPot
     * @return List of GlobalPotMembers.
     */
    List<GlobalPotMembers> findByGlobalPotGlobalPotId(String globalPot);


    /**     * Find GlobalPotMembers by GlobalPot ID and UserRole.
     * @param role
     * @return List of GlobalPotMembers.
     */
    List<GlobalPotMembers> findByGlobalPotGlobalPotIdAndUserRoles(String globalPotId,UserRoles role);




    /**     * Check if GlobalPotMembers exists by User, GlobalPot and UserRole.
     * @param userId
     * @param globalPotId
     * @param userRole
     * @return Boolean.
     */
    Boolean existsByUserUuidAndGlobalPotGlobalPotIdAndUserRoles(
            String userId, String globalPotId, UserRoles userRole);


}
