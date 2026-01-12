package com.bxb.sunduk_pay.Mappers;
//import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Mapper interface for converting User entities and external
 * authentication objects
 * into various response or event objects.
 */
public interface UserMapper {

    /**
     * Converts an OidcUser (from OAuth2 login) into a UserLoginResponse DTO.
     *
     * @param user the OidcUser object from OAuth2 authentication
     * @return a UserLoginResponse DTO.
     */
    UserResponse getUser(OidcUser user);

    /**
     * Converts a UserLoginResponse DTO into a User entity.
     *
     * @param response the UserLoginResponse DTO
     * @return a User entity.
     */
    User toUser(UserResponse response);


//    UserResponse toUserResponse(User user);

   /**
   * Converts a User entity into a Kafka event object for
   * publishing user-related events.
     *
     * @param user      the User entity
 * @param eventType the type of event (e.g., CREATED, UPDATED)
     * @return a UserKafkaEvent object.
     */
    UserKafkaEvent toKafkaEvent(User user, String eventType);

    /**
     * Updates an existing User entity with data from a UserRequest DTO.
     *
     * @param request the UserRequest DTO containing updated user info
     * @param user    the existing User entity to be updated
     * @return the updated User entity.
     */
    User toUpdate(UserRequest request, User user);

    /**
     * Converts a User entity into a detailed UserResponse DTO.
     * @param user the User entity
     * @return a detailed UserResponse DTO.
     */
    UserResponse getDetails(User user);

    /**
     * Converts a User entity into a UserResponse DTO.
     *
     * @param user the User entity
     * @return a UserResponse DTO.
     */
    UserResponse toUserResponse(User user);

}

