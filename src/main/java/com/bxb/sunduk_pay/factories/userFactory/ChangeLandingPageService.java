package com.bxb.sunduk_pay.factories.userFactory;

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
 * Service implementation for handling user operations related
 * to changing the landing page.
 * This class implements the {@link UserOperation}
 * interface and provides the logic
 * for processing user requests that involve changing the landing page.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ChangeLandingPageService implements UserOperation{

    /** Validations for user operations. */
    private final Validations validations;

    /** Repository for User operations. */
    private final UserRepository userRepository;
    /**
     * Retrieves the type of user request this service handles.
     *
     * @return the UserRequestType associated with changing the landing page
     */
    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.CHANGE_LANDING_PAGE;
    }

    /**
     * Performs the operation of changing the landing page
     * based on the provided user request.
     * @param userRequest the user request containing details
     *                    for changing the landing page
     * @return a UserResponse indicating the result of the operation
     */
    @Override
    public UserResponse perform(UserRequest userRequest) {
        User user = validations.getUserInfo(userRequest.getUuid());
        if (userRequest.getLandingPage() != null) {
            log.info("Changing landing page for user: {}",
                    user.getUuid());
            user.setPreferredLandingPage(userRequest.getLandingPage());
        }
        userRepository.save(user);
        return UserResponse.builder().message(
                "Landing page updated successfully").build();
    }
}
