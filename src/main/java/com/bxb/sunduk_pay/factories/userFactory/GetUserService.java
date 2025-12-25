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

@Service
@RequiredArgsConstructor
@Log4j2
public class GetUserService implements UserOperation {

private final Validations validations;

private final MpinValidations mpinValidations;

private final UserMapper mapper;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.USER_DETAILS;
    }

    @Override
    public UserResponse perform(final UserRequest userRequest) {

        User user = validations.getUserInfo(userRequest.getUuid());

        mpinValidations.validateMpin(user.getUuid(),
                userRequest.getMpin());

       return mapper.getDetails(user);
    }
}
