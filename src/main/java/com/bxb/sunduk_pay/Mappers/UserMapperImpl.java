package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.event.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {
    public UserLoginResponse getUser(OidcUser user) {

UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setEmail( user.getEmail() );
        userLoginResponse.setFullName( user.getFullName() );
        userLoginResponse.setPhoneNumber(userLoginResponse.getPhoneNumber());

        return userLoginResponse;
    }


    @Override
    public User toUser(UserLoginResponse response) {
        User user = new User();
        user.setFullName(response.getFullName());
        user.setEmail(response.getEmail());
        user.setPhoneNumber(response.getPhoneNumber());
        return user;
    }


    public UserResponse toUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
//        response.setPhoneNumber(user.getPhoneNumber());
        response.setUuid(user.getUuid());
//        response.setGender(user.getGender());
        return response;
    }

    public List<UserResponse> toUserResponseList(List<User> users){
        List<UserResponse> responses = new ArrayList<>(users.size());
        for (User user : users){
            responses.add(toUserResponse(user));
        }
        return responses;
    }

    public UserKafkaEvent toKafkaEvent(User user,String eventType){
        UserKafkaEvent kafkaEvent=new UserKafkaEvent();
        kafkaEvent.setEmail(user.getEmail());
        kafkaEvent.setUuid(user.getUuid());
        kafkaEvent.setFullName(user.getFullName());
        kafkaEvent.setEventType(eventType);
        return kafkaEvent;
    }


}
