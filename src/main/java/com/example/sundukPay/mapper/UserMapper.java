package com.example.sundukPay.mapper;


import com.example.sundukPay.response.UserLoginResponse;
import org.mapstruct.Mapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


@Mapper(componentModel = "spring")
public interface UserMapper {
//while using first time use Ctrl+Shift+F9 for reloading the mapper

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
