package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.encryption.HashUtil;
import com.bxb.sunduk_pay.encryption.UserInfoEncryption;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.EmailCategory;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Implementation of UserMapper for converting between User entities,
 * OAuth2 user info, and response or event DTOs.
 */
@Component
@RequiredArgsConstructor
@Log4j2
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

    @Override
    public User toUser(UserRequest request) {
        User user =
                User.builder()
                        .fullName(request.getFullName())
                        .dateOfBirth(request.getDateOfBirth()
                                .toString()).email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .city(request.getCity())
                        .country(request.getCountry())
                        .userRole(UserRoles.NORMAL_USER)
                        .build();
        return user;
    }

    /**
     * Converts a User entity to a
     * UserKafkaEvent for event messaging.
     *
     * @param user      the User entity to convert
     * @param eventType the type
     *                  of event (e.g., "USER_CREATED", "USER_UPDATED")
     * @return the corresponding UserKafkaEvent
     */
    public UserKafkaEvent toKafkaEvent(final User user,
                                       final String eventType) {
        UserKafkaEvent kafkaEvent = new UserKafkaEvent();
        kafkaEvent.setEmail(user.getEmail());
        kafkaEvent.setUuid(user.getUuid());
        kafkaEvent.setFullName(user.getFullName());
        kafkaEvent.setEventType(eventType);
        if ((eventType.equals("LOGIN"))) {
            kafkaEvent.setEmailCategory(EmailCategory.SECURITY);
        } else {
            kafkaEvent.setEmailCategory(EmailCategory.WELCOME);
        }
        return kafkaEvent;
    }

    /**
     * Updates a User entity based on a UserRequest DTO.
     *
     * @param request the UserRequest containing updated info
     * @param user    the existing User entity to update
     * @return the updated User entity
     */
    @Override
    public User toUpdate(final UserRequest request, final User user) {

        log.info("Mapping UserRequest to User entity for update | userUuid={}",
                user.getUuid());

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhoneNumber() != null) {
            String phoneNumber =
                    userEncryption.encrypt(request.getPhoneNumber());
            user.setPhoneNumber(phoneNumber);

            String hashPhoneNumber = hashUtil.sha256(request.getPhoneNumber());
            user.setPhoneNumberHash(hashPhoneNumber);
        }


        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM" +
                    " yyyy");
            String dobString = request.getDateOfBirth().format(formatter);
            String encryptedDob = userEncryption.encrypt(dobString);
            user.setDateOfBirth(encryptedDob);
        }


        if (request.getAddress() != null) {
            String presentAddress =
                    userEncryption.encrypt(request.getAddress());
            user.setAddress(presentAddress);
        }

        return user;
    }

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity to convert
     * @return the corresponding UserResponse DTO
     */
    @Override
    public UserResponse getDetails(final User user) {

        String phone = user.getPhoneNumber() == null ? "" :
                userEncryption.decrypt(user.getPhoneNumber());

        String dob = user.getDateOfBirth() == null ? "" :
                userEncryption.decrypt(user.getDateOfBirth());

        String permanentAddress = user.getAddress() == null ? "" :
                userEncryption.decrypt(user.getAddress());


        String photoBase64 =
                (user.getProfilePhoto() != null &&
                        user.getProfilePhoto().length > 0)


                ? "data:image/jpeg;base64," + Base64.getEncoder()
                        .encodeToString(user.getProfilePhoto()) : "";

        return UserResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(phone)
                .address(permanentAddress)
                .dob(dob)
                .profilePhoto(photoBase64)
                .build();
    }


    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity to convert
     * @return the corresponding UserResponse DTO
     */
    public UserResponse toUserResponse(final User user) {
        return UserResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User toUserForm(User user, UserRequest request) {
        if (request != null) {
            if (request.getFullName() != null &&
                    !request.getFullName().isBlank()) {
                user.setFullName(request.getFullName());
            }
            if (request.getPhoneNumber() != null &&
                    !request.getPhoneNumber().isBlank()) {
                String encryptedPhoneNumber = userEncryption.encrypt(
                        request.getPhoneNumber());
                user.setPhoneNumber(encryptedPhoneNumber);
                String phoneNumberHash = hashUtil.sha256(
                        request.getPhoneNumber());
                user.setPhoneNumberHash(phoneNumberHash);
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            user.setUserRole(UserRoles.NORMAL_USER);

            if (request.getDateOfBirth()!= null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                        "dd MMM" + " yyyy");
                String dobEncrypted = userEncryption.encrypt(
                        request.getDateOfBirth().format(formatter));
                user.setDateOfBirth(dobEncrypted);
            }
            if (request.getCity() !=null) {
                user.setCity(request.getCity());
            }
            if (request.getCountry() != null) {
                user.setCountry(request.getCountry());
            }
            if (request.getAddress() != null) {
                String adddressEncrypted = userEncryption.encrypt(
                        request.getAddress());
                user.setAddress(adddressEncrypted);
            }
            if (user.getUserStatus() != UserStatus.ACTIVE) {
                user.setUserStatus(UserStatus.ACTIVE);
            }
            if (user.getIsDeleted() == null) {
            user.setIsDeleted(false);
            }
            if (user.getIsBlocked() == null) {
                user.setIsBlocked(false);
            }
        }

        return user;
    }
}


