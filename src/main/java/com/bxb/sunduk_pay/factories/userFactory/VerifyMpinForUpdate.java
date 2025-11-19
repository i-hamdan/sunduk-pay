package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyMpinForUpdate implements UserOperation{

    private final MpinValidations mpinValidations;

    private final Validations validations;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.VERIFY_MPIN;
    }

    @Override
    public UserResponse perform(UserRequest userRequest) {
        User user = validations.getUserInfo(userRequest.getUuid());
        mpinValidations.validateMpin(user.getUuid(), userRequest.getMpin());
        return UserResponse.builder()
                .message("MPIN verified successfully")
                .build();
    }
}
