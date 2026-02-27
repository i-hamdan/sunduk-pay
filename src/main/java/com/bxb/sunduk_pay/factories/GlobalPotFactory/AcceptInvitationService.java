package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

/***
 * Service to handle the acceptance of an invitation to join a Global Pot.
 * This service performs necessary validations to ensure that the user and
 * Global Pot are valid, and that the user is a member of the Global Pot before
 * accepting the invitation.
 */

@Service
@AllArgsConstructor
@Log4j2
public class AcceptInvitationService implements GlobalPotOperation{

    /** Validations for general checks. */
    private final Validations validations;

    /** Validations specific to global pot operations. */
    private final GlobalPotValidations globalPotValidations;

    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {

        log.info( "AcceptInvitationService identified as " +
                        "handler for GlobalPotRequestType.ACCEPT_INVITATION");

        return GlobalPotRequestType.ACCEPT_INVITATION;
    }

    /***
     * This method handles the acceptance of an invitation to join a Global Pot.
     * It performs several validations to ensure the user and Global Pot are
     * valid and that the user is a member of the Global Pot before accepting
     * the invitation.
     *
     * @param request the GlobalPotRequest containing the details of the
     * invitation acceptance
     * @return a GlobalPotResponse indicating the result of the operation
     * @throws IOException if an error occurs during processing
     */
    @Override
    public GlobalPotResponse perform(GlobalPotRequest request)
            throws IOException {

        log.info("Accepting invitation for user with UUID:" +
                " {} to join Global Pot with ID: {}",
                request.getUuid(), request.getGlobalPotId());


        User user = validations.getUserInfo(request.getUuid());

        log.info( "User with UUID: {} found. " +
                        "Proceeding with invitation acceptance.",
                request.getUuid());


        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());

        log.info( "Global Pot with ID: {} found. " +
                        "Validating user membership.",
                request.getGlobalPotId());


        globalPotValidations.ensureUserIsMember(user,globalPot);

        log.info( "User with UUID: {} is a member " +
                        "of Global Pot with ID: {}. " +
                        "Accepting invitation.",
                request.getUuid(), request.getGlobalPotId());

        return GlobalPotResponse.builder()
                .status("SUCCESS")
                .message("Invitation accepted successfully. " +
                        "User can now participate in the Global Pot: "
                        + globalPot.getCaseTitle())
                .build();
    }
}
