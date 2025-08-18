package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
// this needs to remove once we moved to domain
public class SundukController {
    private final UserService service;
    private final UserMapper userMapper;


    public SundukController(UserService service, UserMapper userMapper) {
        this.service = service;

        this.userMapper = userMapper;
    }
    @GetMapping(value = "/custom-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> login(HttpSession session, @AuthenticationPrincipal OidcUser user , HttpServletResponse httpServletResponse) throws IOException {
        UserLoginResponse response = userMapper.getUser(user);
        log.info ("Session Id : " + session.getId() + " By: " + user.getFullName());
        service.userLogin(response);
        String deepLink = "islamicbank://login-success?sessionId=" + session.getId()
                + "&email=" + URLEncoder.encode(user.getEmail(), "UTF-8")
                + "&fullName=" + URLEncoder.encode(user.getFullName(), "UTF-8");

        log.info("Redirecting to deep link:{}",deepLink);
        httpServletResponse.sendRedirect(deepLink);
        return ResponseEntity.ok().body(response);

    }


    @GetMapping("/custom-logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "You have been logged out";
    }
}