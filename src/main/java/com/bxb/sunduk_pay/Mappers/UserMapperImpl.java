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

    @Override
    public UserLoginResponse getUser(OidcUser user) {
        UserLoginResponse response = new UserLoginResponse();
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        return response;
    }

    @Override
    public User toUser(UserLoginResponse response) {
        User user = new User();
        user.setFullName(response.getFullName());
        user.setEmail(response.getEmail());
        user.setPhoneNumber(null); // Optional: set if available
        user.setIsDeleted(false);  // Default value for new users
        return user;
    }

    @Override
    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUuid(user.getUuid());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return response;
    }

    /**
     * Converts a list of User entities into a list of UserResponse DTOs.
     *
     * @param users the list of User entities
     * @return list of UserResponse DTOs
     */
    public List<UserResponse> toUserResponseList(List<User> users) {
        List<UserResponse> responses = new ArrayList<>(users.size());
        for (User user : users) {
            responses.add(toUserResponse(user));
        }
        return responses;
    }

    @Override
    public UserKafkaEvent toKafkaEvent(User user, String eventType) {
        UserKafkaEvent kafkaEvent = new UserKafkaEvent();
        kafkaEvent.setUuid(user.getUuid());
        kafkaEvent.setFullName(user.getFullName());
        kafkaEvent.setEmail(user.getEmail());
        kafkaEvent.setEventType(eventType);
        return kafkaEvent;
    }
}
