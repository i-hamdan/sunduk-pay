package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component to verify MPIN for user update operations.
 */
@Component
@RequiredArgsConstructor
public class VerifyMpinForUpdate implements UserOperation {

    /** MPIN validation utility. */
    private final MpinValidations mpinValidations;

    /** Validation utility. */
    private final Validations validations;

    /**
     * Returns the type of user request this operation handles.
     *
     * @return UserRequestType associated with this operation.
     */
    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.VERIFY_MPIN;
    }

    /**
     * Verifies the MPIN provided in the user request.
     *
     * @param userRequest the request containing user details and MPIN
     * @return UserResponse indicating the result of the verification
     */
    @Override
    public UserResponse perform(final UserRequest userRequest) {
        User user = validations.getUserInfo(userRequest.getUuid());
        mpinValidations.validateMpin(user.getUuid(), userRequest.getMpin());
        return UserResponse.builder()
                .message("MPIN verified successfully")
                .build();
    }
}
