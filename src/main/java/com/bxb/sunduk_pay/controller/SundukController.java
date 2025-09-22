package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Controller for handling user login, logout, and contact upload.
 */

@Log4j2
@RestController
<<<<<<< Updated upstream
@RequiredArgsConstructor
=======
>>>>>>> Stashed changes
// this needs to remove once we moved to domain
public class SundukController {
    /**
     * Service for user-related operations.
     */
    private final UserService service;

    /**
     * Mapper for converting OIDC user info to application user models.
     */
    private final UserMapper userMapper;

<<<<<<< Updated upstream
=======
    /**
     * Constructor for SundukController.
     *
     * @param service UserService for user operations
     * @param userMapper UserMapper for mapping user data
     */
    public SundukController(final UserService service,
                            final UserMapper userMapper) {
        this.service = service;

        this.userMapper = userMapper;
    }

>>>>>>> Stashed changes

    /**
     * Handles custom login via OIDC.
     *
     * @param session             current HTTP session
     * @param httpServletResponse HTTP response to send redirect
     * @return user login response
     * @throws IOException if redirect fails
     */
<<<<<<< Updated upstream
    @GetMapping(value = "/custom-login", produces = MediaType
            .APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> login(final HttpSession session,
                                                   @AuthenticationPrincipal
                                                   final OidcUser user, final
                                                   HttpServletResponse
                                httpServletResponse) throws IOException {
=======
    @GetMapping(value = "/custom-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> login(final HttpSession session,
                                                   @AuthenticationPrincipal final OidcUser user, final
                                                   HttpServletResponse
                                                               httpServletResponse) throws IOException {
>>>>>>> Stashed changes
        UserLoginResponse response = userMapper.getUser(user);
        log.info("Session Id : " + session.getId() + " By: "
                + user.getFullName());
        User dbUser = service.userLogin(response);
        response.setUuid(dbUser.getUuid());
<<<<<<< Updated upstream
        String deepLink = "islamicbank://login-success?sessionId = "
                + session.getId()
                + "&email = " + URLEncoder.encode(user.getEmail(),
                "UTF-8")
                + "&fullName = " + URLEncoder.encode(user.getFullName(),
                "UTF-8")
                + "&uuid = " + URLEncoder.encode(dbUser.getUuid(),
                "UTF-8");
=======
        String deepLink = "islamicbank://login-success?sessionId = " + session.getId()
                + "&email = " + URLEncoder.encode(user.getEmail(), "UTF-8")
                + "&fullName = " + URLEncoder.encode(user.getFullName(), "UTF-8")
                + "&uuid = " + URLEncoder.encode(dbUser.getUuid(),"UTF-8");
>>>>>>> Stashed changes

        log.info("Redirecting to deep link:{}", deepLink);
        httpServletResponse.sendRedirect(deepLink);
        return ResponseEntity.ok().body(response);

    }

    /**
     * Uploads contacts for the authenticated user.
     *
     * @param contactRequest contact request payload
     * @return user response after upload
     */
<<<<<<< Updated upstream
    @PostMapping("/upload-contact")
    public UserResponse uploadContacts(@RequestBody final
                              ContactRequest contactRequest) {
        return service.uploadContacts(contactRequest);
    }

=======
@PostMapping("/upload-contact")
public UserResponse uploadContacts(@RequestBody final ContactRequest contactRequest) {
    return service.uploadContacts(contactRequest);
}
>>>>>>> Stashed changes
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
