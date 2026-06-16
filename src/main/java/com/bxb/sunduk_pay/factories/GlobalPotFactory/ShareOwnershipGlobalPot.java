package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service to handle sharing ownership of a global pot with another user.
 */
@Service
@RequiredArgsConstructor
public class ShareOwnershipGlobalPot implements GlobalPotOperation {

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * General validations utility.
     */
    private final Validations validations;
    /** Repository to save User data. */
    private final UserRepository userRepository;

    private final GlobalPotMembersRepository globalPotMembersRepository;

    /**
     * Returns the type of global pot request this operation handles.
     *
     * @return GlobalPotRequestType associated with this operation.
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.SHARE_OWNERSHIP;
    }


    /**
     * Shares ownership of a global pot with another user.
     *
     * @param request the request containing admin and target user UUIDs
     * @return GlobalPotResponse indicating the result of the operation
     * @throws IOException if an I/O error occurs during the operation
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        //Checking current admin info.
        User admin = validations.getUserInfo(request.getAdminUuid());

        GlobalPot globalPot = globalPotValidations.getGlobalPot(request.getGlobalPotId());

        //Check if admin is admin
        globalPotValidations.validateAdmin(admin, globalPot);

        //Checking new admin info
        User targetUser = validations.getUserInfo(request.getTargetUserUuid());

        GlobalPotMembers member = globalPotValidations
                .getMemberByUuidAndGlobalPotId(targetUser,globalPot);

        //Share ownership with targeted user.
//        targetUser.setUserRole(UserRoles.GLOBALPOT_ADMIN);

        member.setUserRoles(UserRoles.GLOBALPOT_ADMIN);


        globalPotMembersRepository.save(member);
        return GlobalPotResponse.builder()
                .message("Ownership shared successfully with user:"
                        + targetUser.getUuid())
                .build();
    }
}
