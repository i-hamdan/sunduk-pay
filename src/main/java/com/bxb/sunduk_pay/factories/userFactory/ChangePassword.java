package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.exception.InvalidCredentialsException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ChangePassword implements UserOperation {

    /**
     * Validation utility for user-related checks.
     */
    private final Validations validations;
    /**
     * PasswordEncoder for hashing and verifying passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Repository for user-related database operations.
     */
    private final UserRepository userRepository;


    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.CHANGE_PASSWORD;
    }

    @Override
    public UserResponse perform(UserRequest userRequest) {

        log.info("Change password request received for user UUID: {}",
                userRequest.getUuid());

        User user = validations.getUserInfo(userRequest.getUuid());
        // Validate the old password
        if (!passwordEncoder.matches(userRequest.getOldPassword(),
                user.getPassword())) {

            log.info("Invalid old password provided for user UUID: {}",
                    userRequest.getUuid());

            throw new InvalidCredentialsException("Invalid old password " +
                    "provided.");
        }
        // Update the password
        user.setPassword(passwordEncoder.encode(userRequest.getNewPassword()));

        userRepository.save(user);
        log.info("Password changed successfully for user UUID: {}",
                userRequest.getUuid());

        return UserResponse.builder().message("Password changed successfully" +
                ".").build();
    }
}
