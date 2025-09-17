package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for handling user login, logout, and contact upload.
 */
@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
// TODO: Remove CORS once moved to production domain
public class SundukController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Constructor for SundukController.
     *
     * @param userService service for user operations
     * @param userMapper  mapper for converting OIDC user to local user response
     */
    public SundukController(final UserService userService, final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Handles custom login via OIDC.
     *
     * @param session            current HTTP session
     * @param oidcUser           authenticated OIDC user
     * @param httpServletResponse HTTP response to send redirect
     * @return user login response
     * @throws IOException if redirect fails
     */
    @GetMapping(value = "/custom-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> login(
            final HttpSession session,
            @AuthenticationPrincipal final OidcUser oidcUser,
            final HttpServletResponse httpServletResponse
    ) throws IOException {

        final UserLoginResponse response = userMapper.getUser(oidcUser);
        log.info("Session Id: {} By: {}", session.getId(), oidcUser.getFullName());

        final User dbUser = userService.userLogin(response);
        response.setUuid(dbUser.getUuid());

        final String deepLink = "islamicbank://login-success?sessionId=" + session.getId()
                + "&email=" + URLEncoder.encode(oidcUser.getEmail(), StandardCharsets.UTF_8)
                + "&fullName=" + URLEncoder.encode(oidcUser.getFullName(), StandardCharsets.UTF_8)
                + "&uuid=" + URLEncoder.encode(dbUser.getUuid(), StandardCharsets.UTF_8);

        log.info("Redirecting to deep link: {}", deepLink);
        httpServletResponse.sendRedirect(deepLink);

        return ResponseEntity.ok(response);
    }

    /**
     * Uploads contacts for the authenticated user.
     *
     * @param contactRequest contact request payload
     * @return user response after upload
     */
    @PostMapping("/upload-contact")
    public UserResponse uploadContacts(@RequestBody final ContactRequest contactRequest) {
        return userService.uploadContacts(contactRequest);
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
