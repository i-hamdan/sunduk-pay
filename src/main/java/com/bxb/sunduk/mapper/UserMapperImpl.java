package com.bxb.sunduk.mapper;

import com.bxb.sunduk.response.UserLoginResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements  UserMapper{
    public UserLoginResponse toResponse(OidcUser user) {
        if ( user == null ) {
            return null;
        }

        UserLoginResponse userLoginResponse = new UserLoginResponse();

        userLoginResponse.setEmail( user.getEmail() );
        userLoginResponse.setFullName( user.getFullName() );

        return userLoginResponse;
    }
}
