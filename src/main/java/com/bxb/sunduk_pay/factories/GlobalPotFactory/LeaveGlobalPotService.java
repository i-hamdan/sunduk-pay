package com.bxb.sunduk_pay.factories.GlobalPotFactory;

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

/**
 * LeaveGlobalPotService allows a user to leave a Global Pot.
 *
 * This service validates the user's membership in the Global Pot
 * and removes them from the members list, effectively allowing them
 * to exit the Global Pot.
 */
@Service
@AllArgsConstructor
@Log4j2
public class LeaveGlobalPotService implements GlobalPotOperation{

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
     * @return LEAVE_GLOBAL_POT request type
     */



    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {

        log.info("LeaveGlobalPotService is handling request type: {}",
                GlobalPotRequestType.LEAVE_GLOBAL_POT);

        return GlobalPotRequestType.LEAVE_GLOBAL_POT;
    }

    /**
     * Performs the operation to allow a user to leave a Global Pot.
     *
     * @param request the Global Pot request containing necessary details
     *                such as user ID and Global Pot ID.
     * @return a response indicating the result of the leave operation.
     * @throws IOException if an I/O error occurs during processing.
     */

    @Override
    public GlobalPotResponse perform(GlobalPotRequest request)
            throws IOException {

            log.info("Processing request to leave Global Pot with ID:"
                    + " {}", request.getGlobalPotId());

        User user = validations.getUserInfo(request.getUuid());

        log.info("User fetched successfully | userUuid={}",
                user.getUuid());

        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());

        log.info("GlobalPot fetched successfully | globalPotId={}",
                globalPot.getGlobalPotId());

        GlobalPotMembers member = globalPotValidations
                .getMemberByUuidAndGlobalPotId(user,globalPot);

        log.info("GlobalPot member fetched successfully | " +
                        "userUuid={} " + "globalPotId={}", user.getUuid(),
                globalPot.getGlobalPotId());

        globalPotMembersRepository.delete(member);

        log.info("User with ID: {} has left Global Pot with ID: {}",
                user.getUuid(), globalPot.getGlobalPotId());

        return GlobalPotResponse.builder()
                .message("User with ID: " + user.getUuid()
                        + " has left Global Pot with ID: "
                        + globalPot.getGlobalPotId())
                .build();

    }
}
