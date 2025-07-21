package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.response.UserLoginResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


public interface UserMapper {
    UserLoginResponse toResponse(OidcUser user);



















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
