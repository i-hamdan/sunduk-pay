package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
// this needs to remove once we moved to domain
public class SundukController {
    private final UserService service;
    private final UserMapper userMapper;


    private static final Logger logger= LoggerFactory.getLogger(SundukController.class);

    public SundukController(UserService service, UserMapper userMapper) {
        this.service = service;

        this.userMapper = userMapper;
    }

    @GetMapping("/custom-login")
    public ResponseEntity<UserLoginResponse> login(HttpSession session, @AuthenticationPrincipal OidcUser user) {
        UserLoginResponse response = userMapper.getUser(user);
        logger.info ("Session Id : " + session.getId() + " By: " + user.getFullName());
        service.login(response);
        return ResponseEntity.ok().body(response);

    }


    @GetMapping("/custom-logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "You have been logged out";
    }
}