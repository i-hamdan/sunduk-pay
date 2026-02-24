package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.exception.UserIsBlockedException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service to block a member in a global pot.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class BlockMemberService implements GlobalPotOperation {

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * General validations utility.
     */
    private final Validations validations;
    /**
     * Repository for managing global pot members.
     */
    private final GlobalPotMembersRepository membersRepository;

    /**
     * Specifies the type of request this service handles.
     *
     * @return GlobalPotRequestType.BLOCK_USER
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.BLOCK_USER;
    }

    /**
     * Perform the block member operation.
     *
     * @param request the global pot request containing details
     *                for blocking a member
     * @return GlobalPotResponse indicating the result of the operation
     */
    @Override
    public GlobalPotResponse perform(final GlobalPotRequest request) {
log.info("Block member request | admin={} pot={} targetUser={}",
        request.getAdminUuid(),
        request.getGlobalPotId(),
        request.getTargetUserUuid());
        User admin = validations.getUserInfo(request.getAdminUuid());

        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());

        globalPotValidations.validateSundukPayAndGlobalPotAdmin
                (admin,globalPot);

        User targetUser =
                validations.getUserInfo(request.getTargetUserUuid());
        log.info("Fetched target user: {}", targetUser.getUuid());

        GlobalPotMembers memberToBeBlocked = globalPotValidations
                .getMemberByUuidAndGlobalPotId(
                targetUser, globalPot);

        log.info("Fetched member to be blocked: {} in pot {}",
                memberToBeBlocked.getUser().getUuid(),
                globalPot.getGlobalPotId());

        if (memberToBeBlocked.getIsBlocked()) {
            log.error("User {} is already blocked in pot {}",
                    targetUser.getUuid(),
                    globalPot.getGlobalPotId());
            throw new UserIsBlockedException(
                    "This user is already blocked in the pot.");
        }

        memberToBeBlocked.setIsBlocked(true);
        log.info("Setting user {} as blocked in pot {}",
                targetUser.getUuid(),
                globalPot.getGlobalPotId());

        membersRepository.save(memberToBeBlocked);
        log.info("User {} has been blocked in pot {} successfully",
                targetUser.getUuid(),
                globalPot.getGlobalPotId());

        return GlobalPotResponse.builder()
                .message("User has been blocked successfully")
                .build();
    }
}
