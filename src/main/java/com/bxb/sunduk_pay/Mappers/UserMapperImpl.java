package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.response.UserLoginResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    public UserLoginResponse getUser(OidcUser user) {

        UserLoginResponse userLoginResponse = new UserLoginResponse();

        userLoginResponse.setEmail( user.getEmail() );
        userLoginResponse.setFullName( user.getFullName() );

        return userLoginResponse;
    }
}
