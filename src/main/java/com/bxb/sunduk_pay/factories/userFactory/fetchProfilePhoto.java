package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Service class to handle fetching of user profile photos.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class fetchProfilePhoto implements UserOperation {

    /** Validation utility. */
    private final Validations validations;

    /**
     * Returns the type of user request this operation handles.
     */
    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.FETCH_PROFILE_PHOTO;
    }

    /**
     * Retrieves the user's profile photo in Base64 format.
     */
    @Override
    public UserResponse perform(UserRequest userRequest) {

        log.info("Fetch profile photo request received for UUID: {}",
                userRequest.getUuid());

        try {
            User user = validations.getUserInfo(userRequest.getUuid());
            log.debug("User fetched successfully for UUID: {}",
                    userRequest.getUuid());

            String photoBase64;

          if (user.getProfilePhoto() != null && user.getProfilePhoto()
                  .length > 0) {
                photoBase64 = "data:image/jpeg;base64,"
                        + Base64.getEncoder()
                        .encodeToString(user.getProfilePhoto());

                log.info("Profile photo found for UUID: {}", userRequest
                        .getUuid());
                log.debug("Profile photo size (bytes): {}", user
                        .getProfilePhoto().length);
            } else {
                photoBase64 = "";
                log.warn("No profile photo available for UUID: {}",
                        userRequest.getUuid());
            }

            return UserResponse.builder()
                    .profilePhoto(photoBase64)
                    .build();

        } catch (Exception e) {
        log.error("Error while fetching profile photo for UUID: {}",
                    userRequest.getUuid(), e);
            throw e; // rethrow so global exception handler can catch it
        }
    }
}
