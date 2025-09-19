package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Mapper interface for converting User entities and external authentication objects
 * into various response or event objects.
 */
public interface UserMapper {

    /**
     * Converts an OidcUser (from OAuth2 login) into a UserLoginResponse DTO.
     *
     * @param user the OidcUser object from OAuth2 authentication
     * @return a UserLoginResponse DTO
     */
    UserLoginResponse getUser(OidcUser user);

    /**
     * Converts a UserLoginResponse DTO into a User entity.
     *
     * @param response the UserLoginResponse DTO
     * @return a User entity
     */
    User toUser(UserLoginResponse response);

    /**
     * Converts a User entity into a UserResponse DTO for API responses.
     *
     * @param user the User entity
     * @return a UserResponse DTO
     */
    UserResponse toUserResponse(User user);

    /**
     * Converts a User entity into a Kafka event object for publishing user-related events.
     *
     * @param user      the User entity
     * @param eventType the type of event (e.g., CREATED, UPDATED)
     * @return a UserKafkaEvent object
     */
    UserKafkaEvent toKafkaEvent(User user, String eventType);



















    //    public UserLoginRequest toLoginRequest(OidcUser user){
    //        UserLoginRequest response=new UserLoginRequest();
    //        response.setFullName(user.getFullName());
    //        response.setEmail(user.getEmail());
    //        return response;
    //    }
//    public User fromLoginRequestToUser(UserLoginRequest request){
//        User user = new User();
//        user.setUuid(UUID.randomUUID().toString());
//        user.setFullName(request.getFullName());
//        user.setEmail(request.getEmail());
//        user.setIsDeleted(false);
//        return user;
//    }
//    public UserResponse toUserResponse(User user){
//        UserResponse response = new UserResponse();
//        response.setFirstName(user.getFullName());
//        response.setEmail(user.getEmail());
//        return response;
//    }


}
