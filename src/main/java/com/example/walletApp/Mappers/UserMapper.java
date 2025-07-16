package com.example.walletApp.Mappers;

import com.example.walletApp.model.User;
import com.example.walletApp.request.UserLoginRequest;
import com.example.walletApp.request.UserRequest;
import com.example.walletApp.response.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {
//while using first time use Ctrl+Shift+F9 for reloading the mapper

    UserLoginRequest toLoginRequest(OidcUser user);

    User toUser(UserLoginRequest request);
    
    User fromUserRequestToUser(UserRequest request);

    List<UserResponse> toUserResponseList(List<User> users);

    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserRequest request,@MappingTarget User user);



















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
