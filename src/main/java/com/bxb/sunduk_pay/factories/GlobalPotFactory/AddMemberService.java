package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.GlobalPot;
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

/**
 * AddMemberService is responsible for allowing a Global Pot admin
 * to manually add a user as a member of a Global Pot.
 *
 * This service validates the admin privileges, verifies the
 * existence of the Global Pot and target user, and ensures
 * the user is added as a member.
 */
@Service
@AllArgsConstructor
@Log4j2
public class AddMemberService implements GlobalPotOperation {

    /**
     * Validation component used to fetch and validate user details.
     */
    private final Validations validations;

    /**
     * Validation component for Global Pot related operations.
     */
    private final GlobalPotValidations globalPotValidations;

    /**
     * Repository for managing Global Pot members.
     */
    private final GlobalPotMembersRepository globalPotMembersRepository;

    /**
     * Returns the Global Pot request type handled by this service.
     *
     * @return ADD_MEMBER request type
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.ADD_MEMBER;
    }

    /**
     * Adds a user as a member of the specified Global Pot.
     *
     * @param request contains admin details, target user,
     *                and Global Pot information
     * @return response indicating success or failure
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        log.info("AddMember started | "
                        + "adminUuid={} globalPotId={} targetUserUuid={}",
                request.getAdminUuid(),
                request.getGlobalPotId(),
                request.getTargetUserToAdd());

        User admin = validations.getUserInfo(request.getAdminUuid());
        log.info("Admin fetched successfully | adminUuid={}",
                admin.getUuid());

        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("GlobalPot fetched successfully | globalPotId={}",
                globalPot.getGlobalPotId());

        globalPotValidations.validateAdmin(admin,globalPot);


        User targetUser =
                validations.getUserInfo(request.getTargetUserToAdd());
        log.info("Target user fetched successfully | userUuid={}",
                targetUser.getUuid());


            globalPotValidations.ensureUserIsMemberByAdmin(
                    targetUser, globalPot);
            log.info("User added as member | userUuid={} globalPotId={}",
                    targetUser.getUuid(),
                    globalPot.getGlobalPotId());


        return GlobalPotResponse.builder()
                .message("User added to Global Pot successfully")
                .status("SUCCESS")
                .build();
    }
}
