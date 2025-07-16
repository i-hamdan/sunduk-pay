package com.example.walletApp.controller;


import com.example.walletApp.Mappers.UserMapper;
import com.example.walletApp.request.UserLoginRequest;
import com.example.walletApp.response.UserResponse;
import com.example.walletApp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@RestController
@CrossOrigin(origins = "http://localhost:5174",allowCredentials = "true")
public class SundukController {
private final UserService userService;
private final UserMapper userMapper;

    public SundukController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/custom-login")
    public UserResponse login(HttpSession session, @AuthenticationPrincipal OidcUser user){
        UserLoginRequest request = userMapper.toLoginRequest(user);
        System.out.println("Session Id : "+session.getId());
        return userService.login(request);
    }

    @GetMapping("/check")
    public String check() {
        System.out.println("api hit Successfully");
        return "hello";
    }




    @GetMapping("/custom-logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "You have been logged out";
    }
}