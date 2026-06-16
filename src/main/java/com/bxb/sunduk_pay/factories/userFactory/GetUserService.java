package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service class to handle retrieval of user details.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class GetUserService implements UserOperation {

    /** Validation utility. */
private final Validations validations;
/** MPIN validation utility. */
private final MpinValidations mpinValidations;
/** Mapper for user data transformations. */
private final UserMapper mapper;

/**
     * Returns the type of user request this operation handles.
     *
     * @return UserRequestType associated with this operation.
     */
    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.USER_DETAILS;
    }

    /**
     * Retrieves user details after validating the provided MPIN.
     *
     * @param userRequest the request containing user UUID and MPIN
     * @return UserResponse containing user details
     */
    @Override
    public UserResponse perform(final UserRequest userRequest) {

        User user = validations.getUserInfo(userRequest.getUuid());

        mpinValidations.validateMpin(user.getUuid(),
                userRequest.getMpin());

       return mapper.getDetails(user);
    }
}
