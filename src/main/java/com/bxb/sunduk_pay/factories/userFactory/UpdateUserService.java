package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
/**
 * UpdateUserService handles the updating of user information.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UpdateUserService implements UserOperation {
    /**
     * Validations instance for user validation
     */
    private final Validations validations;
    /**
     * MpinValidations instance for mpin validation
     */
    private final MpinValidations mpinvalidations;
/**
     * UserMapper for converting between User entities and requests
     */

    private final UserMapper mapper;
    /**
     * UserRepository for database operations
     */
    private final UserRepository userRepository;
/**
     * Returns the type of user request this operation handles.
     * @return UserRequestType
     */
    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.UPDATE;
    }
/**
     * Performs the user update operation based on the provided request.
     * @param userRequest
     * @return UserResponse
     */
    @Override
    public UserResponse perform(final UserRequest userRequest) {

        User user = validations.getUserInfo(userRequest.getUuid());


        //mpinvalidations.validateMpin(user.getUuid(), userRequest.getMpin());

        User update = mapper.toUpdate(userRequest, user);

        log.info("updated User info of user  " +user.getFullName());
        userRepository.save(update);

        return UserResponse.builder()
                .message("saved new info Successfully")
                .build();
}
}
