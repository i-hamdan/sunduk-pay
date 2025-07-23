package com.bxb.sunduk_pay.controller;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.Mappers.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5174",allowCredentials = "true")
public class SundukController {
    private final ApplicationEventPublisher publisher;
private final UserMapper userMapper;

    public SundukController(ApplicationEventPublisher publisher, UserMapper userMapper) {
        this.publisher = publisher;
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