package com.bxb.sunduk_pay.Mappers;



import com.bxb.sunduk_pay.encryption.HashUtil;
import com.bxb.sunduk_pay.encryption.UserInfoEncryption;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.cli.Digest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Implementation of UserMapper for converting between User entities,
 * OAuth2 user info, and response or event DTOs.
 */
@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    /**
     * UserInfoEncryption for encrypting/decrypting user info.
     */
    private final UserInfoEncryption userEncryption;
    /**
     * HashUtil for hashing operations.
     */
    private final HashUtil hashUtil;

    /**
     *
     * @param user the OidcUser object from OAuth2 authentication.
     * @return a UserLoginResponse containing user info.
     */
    public UserResponse getUser(final OidcUser user) {

UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());

        return userResponse;
    }

/**
     * Converts a UserLoginResponse to a User entity.
     *
     * @param response the UserLoginResponse containing user info
     * @return the corresponding User entity
     */

    @Override
    public User toUser(final UserResponse response) {
        User user = new User();
        user.setFullName(response.getFullName());
        user.setEmail(response.getEmail());
        user.setPhoneNumber(null);
        return user;
    }

/**
     * Converts a User entity to a
     UserKafkaEvent for event messaging.
    * @param user the User entity to convert
     * @param eventType the type
 *    of event (e.g., "USER_CREATED", "USER_UPDATED")
     * @return the corresponding UserKafkaEvent
     */
    public UserKafkaEvent toKafkaEvent(
            final User user, final String eventType) {
        UserKafkaEvent kafkaEvent = new UserKafkaEvent();
        kafkaEvent.setEmail(user.getEmail());
        kafkaEvent.setUuid(user.getUuid());
        kafkaEvent.setFullName(user.getFullName());
        kafkaEvent.setEventType(eventType);
        return kafkaEvent;
    }

    @Override
    public User toUpdate(UserRequest request,User user) {

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhoneNumber() != null) {
            String phoneNumber = userEncryption
                    .encrypt(request.getPhoneNumber());
            user.setPhoneNumber(phoneNumber);

            String hashPhoneNumber = hashUtil
                    .sha256(request.getPhoneNumber());
            user.setPhoneNumberHash(hashPhoneNumber);
        }



        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd MMM yyyy");
            String dobString = request.getDateOfBirth().format(formatter); // e.g. "20 Jul 2025"
            String encryptedDob = userEncryption.encrypt(dobString);
            user.setDateOfBirth(encryptedDob);
        }


        if (request.getPresentAddress()!=null){
            String presentAddress =userEncryption
                    .encrypt(request.getPresentAddress());
            user.setPresentAddress(presentAddress);
  }

        if (request.getPermanentAddress()!=null){
            String permanentAddress = userEncryption
                    .encrypt(request.getPermanentAddress());
            user.setPermanentAddress(permanentAddress);
        }

        return user;
    }

    @Override
    public UserResponse getDetails(User user) {

        String phone = user.getPhoneNumber() == null ?
                ""
                : userEncryption.decrypt(user.getPhoneNumber());

        String dob = user.getDateOfBirth() == null ?
                ""
                : userEncryption.decrypt(user.getDateOfBirth());

        String permanentAddress = user.getPermanentAddress() == null ?
                ""
                :userEncryption.decrypt(user.getPermanentAddress());

        String presentAddress = user.getPresentAddress() == null ?
                ""
                :userEncryption.decrypt(user.getPresentAddress());

        String photoBase64 = (user.getProfilePhoto() != null &&
                user.getProfilePhoto().length > 0)
                ? "data:image/jpeg;base64," + Base64.getEncoder()
                .encodeToString(user.getProfilePhoto())
                : "";

        return UserResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(phone)
                .presentAddress(presentAddress)
                .dob(dob)
                .permanentAddress(permanentAddress)
                .profilePhoto(photoBase64)
                .build();


    }
}

