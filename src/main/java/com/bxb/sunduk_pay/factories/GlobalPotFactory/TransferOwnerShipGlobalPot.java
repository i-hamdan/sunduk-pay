package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.User;
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
    private final UserRepository userRepository;

    /**
     * Transfers ownership of the global pot to another user.
     *
     * @param request the request containing admin and target user UUIDs
     * @return GlobalPotResponse indicating the result of the operation
     * @throws IOException if an I/O error occurs during the operation
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {
        // checking currentAdmin Info
        User admin = validations.getUserInfo(request.getAdminUuid());
        // check if admin is admin
        globalPotValidations.validateAdmin(admin);

        // checking new owner info
        User targetUser = validations.
                getUserInfo(request.getTargetUserUuid());
        // transfering ownership
        targetUser.setUserRole(UserRoles.GLOBALPOT_ADMIN);
        // downgrading previous owner
        admin.setUserRole(UserRoles.NORMAL_USER);

        userRepository.save(admin);

        userRepository.save(targetUser);

        return GlobalPotResponse.builder()
                .message("Ownership transferred successfully to user: "
                        + targetUser.getUuid())
                .build();
    }
}
