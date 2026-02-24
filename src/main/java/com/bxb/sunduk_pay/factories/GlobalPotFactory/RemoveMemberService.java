package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RemoveMemberService allows a Global Pot admin
 * to remove an existing member from a Global Pot.
 *
 * This service validates admin privileges,
 * checks membership existence, and removes
 * the user from the Global Pot members list.
 */
@Service
@AllArgsConstructor
@Log4j2
public class RemoveMemberService implements GlobalPotOperation {

    /**
     * Validation component used to fetch and validate user details.
     */
    private final Validations validations;

    /**
     * Validation component for Global Pot related operations.
     */
    private final GlobalPotValidations globalPotValidations;

    /**
     * Repository used to manage Global Pot members.
     */
    private final GlobalPotMembersRepository globalPotMembersRepository;


    /**
     * Returns the Global Pot request type handled by this service.
     *
     * @return REMOVE_MEMBER request type
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.REMOVE_MEMBER;
    }

    /**
     * Removes a user from the specified Global Pot.
     *
     * @param request contains admin details, target user,
     *                and Global Pot information
     * @return response indicating success or failure
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        log.info("Initiating member removal from Global Pot |" +
                        " adminUuid={} globalPotId={} targetUserUuids={}",
                request.getAdminUuid(), request.getGlobalPotId(),
                request.getTargetUsersToRemove());

        if (request.getTargetUsersToRemove() == null ||
                request.getTargetUsersToRemove().isEmpty()) {
            throw new IllegalArgumentException("No users provided for removal");
        }

        User admin = validations.getUserInfo(request.getAdminUuid());

        log.info("Admin fetched successfully | adminUuid={}",
                admin.getUuid());


        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("GlobalPot fetched successfully | globalPotId={}",
                globalPot.getGlobalPotId());
        globalPotValidations.validateSundukPayAndGlobalPotAdmin
                (admin,globalPot);

List<GlobalPotMembers> membersToRemove = new ArrayList<>();

request.getTargetUsersToRemove().forEach(uuid ->{
   membersToRemove.add(globalPotMembersRepository
           .findByUserUuidAndGlobalPotGlobalPotId(
            uuid, globalPot.getGlobalPotId()).orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Membership record not found for userUuid: " + uuid
                            + " and globalPotId: " +
                                    globalPot.getGlobalPotId())));
});

        globalPotMembersRepository.deleteAll(membersToRemove);
        log.info(
    "User removed from Global Pot successfully | userUuid={} globalPotId={}",
                request.getTargetUsersToRemove(), globalPot.getGlobalPotId());

        return GlobalPotResponse.builder()
                .message("User removed from Global Pot successfully")
                .status("SUCCESS")
                .build();
    }
}
