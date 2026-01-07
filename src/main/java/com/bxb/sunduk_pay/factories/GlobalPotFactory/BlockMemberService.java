package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotBlockedUser;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotBlockedUserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockMemberService implements GlobalPotOperation {

    private final GlobalPotValidations globalPotValidations;
    private final GlobalPotBlockedUserRepository blockedUserRepository;
    private final Validations validations;

    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.BLOCK_USER;
    }

    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) {

        User admin = validations.getUserInfo(request.getAdminUuid());

        globalPotValidations.validateAdmin(admin);

        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());



        globalPotValidations.validateNotAlreadyBlocked(
                request.getGlobalPotId(),
                request.getTargetUserUuid()
        );

        User targetUser =
                validations.getUserInfo(request.getTargetUserUuid());

        blockedUserRepository.save(
                GlobalPotBlockedUser.builder()
                        .globalPot(globalPot)
                        .user(targetUser)
                        .build()
        );

        return GlobalPotResponse.builder()
                .message("User has been blocked successfully")
                .build();
    }
}
