package com.bxb.sunduk.controller;


import com.bxb.sunduk.mapper.UserMapper;
import com.bxb.sunduk.response.UserLoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5174",allowCredentials = "true")
public class SundukController {
private final UserMapper userMapper;

    public SundukController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/custom-login")
    public ResponseEntity<UserLoginResponse> login(HttpSession session, @AuthenticationPrincipal OidcUser user){
        UserLoginResponse response = userMapper.toResponse(user);
        System.out.println("Session Id : "+session.getId() + " By: " + user.getFullName());
        return ResponseEntity.ok().body(response);

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