package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
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

        return userLoginResponse;
    }
    @Override
    public User toUser(UserLoginResponse response) {
        User user = new User();
        user.setFullName(response.getFullName());
        user.setEmail(response.getEmail());
        return user;
    }


    public UserResponse toUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setUuid(user.getUuid());
        response.setGender(user.getGender());
        response.setUserType(user.getUserType());
        return response;
    }

    public List<UserResponse> toUserResponseList(List<User> users){
        List<UserResponse> responses = new ArrayList<>(users.size());
        for (User user : users){
            responses.add(toUserResponse(user));
        }
        return responses;
    }

    public User fromUserRequestToUser(UserRequest request){
        User user = new User();
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setUserType(request.getUserType());
        return user;
    }
    public void updateUserFromRequest(UserRequest request, User user) {

        if ( request.getFullName() != null ) {
            user.setFullName( request.getFullName() );
        }
        if ( request.getGender() != null ) {
            user.setGender( request.getGender() );
        }
        if ( request.getEmail() != null ) {
            user.setEmail( request.getEmail() );
        }
        if ( request.getPhoneNumber() != null ) {
            user.setPhoneNumber( request.getPhoneNumber() );
        }
        if ( request.getUserType() != null ) {
            user.setUserType( request.getUserType() );
        }
    }

}
