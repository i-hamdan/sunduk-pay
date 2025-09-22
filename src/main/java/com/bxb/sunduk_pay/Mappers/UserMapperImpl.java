package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserMapper for converting between User entities,
 * OAuth2 user info, and response or event DTOs.
 */
@Component
public class UserMapperImpl implements UserMapper {
    /**
     *
     * @param user the OidcUser object from OAuth2 authentication
     * @return
     */
    public UserLoginResponse getUser(final OidcUser user) {

UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setEmail(user.getEmail());
        userLoginResponse.setFullName(user.getFullName());

        return userLoginResponse;
    }

/**
     * Converts a UserLoginResponse to a User entity.
     *
     * @param response the UserLoginResponse containing user info
     * @return the corresponding User entity
     */

    @Override
    public User toUser(final UserLoginResponse response) {
        User user = new User();
        user.setFullName(response.getFullName());
        user.setEmail(response.getEmail());
        user.setPhoneNumber(null);
        return user;
    }

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity to convert
     * @return the corresponding UserResponse DTO
     */
    public UserResponse toUserResponse(final User user){
        UserResponse response = new UserResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
//        response.setPhoneNumber(user.getPhoneNumber());
        response.setUuid(user.getUuid());
//        response.setGender(user.getGender());
        return response;
    }
/**
     * Converts a list of User entities to a list of UserResponse DTOs.
     *
     * @param users the list of User entities to convert
     * @return the corresponding list of UserResponse DTOs
     */
    public List<UserResponse> toUserResponseList( final List<User> users){
        List<UserResponse> responses = new ArrayList<>(users.size());
        for (User user : users){
            responses.add(toUserResponse(user));
        }
        return responses;
    }
/**
     * Converts a User entity to a UserKafkaEvent for event messaging.
     *
     * @param user the User entity to convert
     * @param eventType the type of event (e.g., "USER_CREATED", "USER_UPDATED")
     * @return the corresponding UserKafkaEvent
     */
    public UserKafkaEvent toKafkaEvent( final User user, final String eventType){
        UserKafkaEvent kafkaEvent = new UserKafkaEvent();
        kafkaEvent.setEmail(user.getEmail());
        kafkaEvent.setUuid(user.getUuid());
        kafkaEvent.setFullName(user.getFullName());
        kafkaEvent.setEventType(eventType);
        return kafkaEvent;
    }
}

