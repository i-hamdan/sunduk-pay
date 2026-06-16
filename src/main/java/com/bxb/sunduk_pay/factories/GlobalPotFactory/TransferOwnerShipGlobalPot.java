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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TransferOwnerShipGlobalPot implements GlobalPotOperation {

    /**
     * Returns the type of global pot request this operation handles.
     *
     * @return GlobalPotRequestType.TRANSFER_OWNERSHIP
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.TRANSFER_OWNERSHIP;
    }
    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * General validations utility.
     */
    private final Validations validations;
    /** Repository to save User data. */
    private final GlobalPotMembersRepository globalPotMembersRepository;

    /**
     * Transfers ownership of the global pot to another user.
     *
     * @param request the request containing admin and target user UUIDs
     * @return GlobalPotResponse indicating the result of the operation
     * @throws IOException if an I/O error occurs during the operation
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {


        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());

        // checking currentAdmin Info
        User admin = validations.getUserInfo(request.getAdminUuid());
        globalPotValidations.validateAdmin(admin,globalPot);

        GlobalPotMembers adminMember = globalPotValidations
                .getMemberByUuidAndGlobalPotId(admin, globalPot);
        adminMember.setUserRoles(UserRoles.NORMAL_USER);


        // checking new owner info
        User targetUser = validations.
                getUserInfo(request.getTargetUserUuid());

        GlobalPotMembers targetMember = globalPotValidations
                .getMemberByUuidAndGlobalPotId(targetUser, globalPot);
        // transferring ownership
        targetMember.setUserRoles(UserRoles.GLOBALPOT_ADMIN);

        globalPotMembersRepository.saveAll(
                java.util.List.of(adminMember, targetMember)
        );

        return GlobalPotResponse.builder()
                .message("Ownership transferred successfully to user: "
                        + targetUser.getUuid())
                .build();
    }
}
