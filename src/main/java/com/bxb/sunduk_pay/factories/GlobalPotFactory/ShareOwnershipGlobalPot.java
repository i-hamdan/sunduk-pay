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
public class ShareOwnershipGlobalPot implements GlobalPotOperation{

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * General validations utility.
     */
    private final Validations validations;
    /** Repository to save User data */
    private final UserRepository userRepository;

    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.SHARE_OWNERSHIP;
    }


    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) throws IOException {

        //Checking current admin info.
        User admin = validations.getUserInfo(request.getAdminUuid());

        //Check if admin is admin
        globalPotValidations.validateAdmin(admin);

        //Checking new admin info
        User targetUser = validations.getUserInfo(request.getTargetUserForAdmin());

        //Share ownership with targeted user.
        targetUser.setUserRole(UserRoles.GLOBALPOT_ADMIN);

        userRepository.save(targetUser);
        return GlobalPotResponse.builder()
                .message("Ownership shared successfully with user:"
                        + targetUser.getUuid())
                .build();
    }
}
