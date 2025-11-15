package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Controller for handling user login, logout.
 */

@Log4j2
@RestController
@RequiredArgsConstructor
// this needs to remove once we moved to domain
public class SundukController {
    /**
     * Service for user-related operations.
     */
    private final UserService service;
    /**
     * Mapper for converting OIDC user info
     * to application user model.
     */
    private final UserMapper userMapper;


    /**
     * Handles custom login via OIDC.
     *
     * @param session             current HTTP session
     * @param httpServletResponse HTTP response to send redirect
     * @param user                authenticated OIDC user
     * @return user login response
     * @throws IOException if redirect fails
     */
    @GetMapping(value = "/custom-login",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> login(
            final HttpSession session,
            @AuthenticationPrincipal final OidcUser user,
            final HttpServletResponse httpServletResponse)
            throws IOException {
        UserResponse response = userMapper.getUser(user);
        log.info("Session Id : "
                + session.getId()
                + " By: "
                + user.getFullName());
        User dbUser = service.userLogin(response);
        response.setUuid(dbUser.getUuid());

        boolean isMpinCreated = dbUser.getIsMpinCreated();
        String phoneNumber = dbUser.getPhoneNumber();

        String deepLink = "islamicbank://login-success?sessionId="
                + session.getId()
                + "&email=" + URLEncoder.encode(user.getEmail(),
                "UTF-8")
                + "&fullName=" + URLEncoder.encode(user.getFullName(),
                "UTF-8")
                + "&uuid=" + URLEncoder.encode(dbUser.getUuid().toString(),
                "UTF-8")
                + "&phoneNumber=" + URLEncoder.encode(
                (phoneNumber != null) ? phoneNumber : "", "UTF-8")
                + "&isMpinCreated=" + isMpinCreated;


        log.info("Redirecting to deep link:{}", deepLink);
        httpServletResponse.sendRedirect(deepLink);
        return ResponseEntity.ok().body(response);
    }


    /**
     * Logs out the current user by invalidating the session.
     *
     * @param session current HTTP session
     * @return logout confirmation message
     */
    @GetMapping("/custom-logout")
    public String logout(final HttpSession session) {
        session.invalidate();
        return "You have been logged out";
    }
}