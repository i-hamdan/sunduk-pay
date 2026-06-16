package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import com.bxb.sunduk_pay.service.OtpService;
import com.bxb.sunduk_pay.service.UserService;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * SundukController handles authentication and user-related API endpoints,
 * including custom login/logout, user existence checks, signup initiation,
 * OTP verification, and login operations.
 */

@Log4j2
@RestController
@RequiredArgsConstructor
// this needs to remove once we moved to domain
public class SundukController {

    /**
     * Service for OTP-related operations.
     */
    private final OtpService otpService;
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
     * Service for authorised session operation.
     */
    private final AuthenticationSessionService authenticationSessionService;
    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;
    /**
     * Validation utility for user-related checks.
     */
    private final Validations validations;

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
            final HttpServletResponse httpServletResponse,
            final UserRequest userRequest)
            throws IOException {

        UserResponse response = userMapper.getUser(user);

        log.info("Session Id : " + session.getId() + " By: "
                + user.getFullName());

        User dbUser = service.userLogin(response);


        response.setUuid(dbUser.getUuid());


        boolean isMpinCreated = dbUser.getIsMpinCreated();
        String phoneNumber = dbUser.getPhoneNumber();
        String landingPage = dbUser.getPreferredLandingPage();

        authenticationSessionService.saveSession(session.getId(), dbUser);

        String deepLink;
        if (phoneNumber == null){
            deepLink= "islamicbank://signup-profile?sessionId="
                    + session.getId()
                    + "&email=" + URLEncoder.encode(user.getEmail(),
                    "UTF-8")
                    + "&fullName=" + URLEncoder.encode(user.getFullName(),
                    "UTF-8")
                    + "&uuid=" + URLEncoder.encode(dbUser.getUuid().toString(),
                    "UTF-8")
                    + "&phoneNumber=" + URLEncoder.encode(
                    (phoneNumber != null) ? phoneNumber : "", "UTF-8")
                    +"&defaultLandingPage" + URLEncoder.encode(
                    (landingPage != null) ? landingPage : "", "UTF-8")
                    + "&isMpinCreated=" + isMpinCreated;
        }
        else {
            deepLink =
                    "islamicbank://login-success?sessionId=" + session.getId() +
                            "&email=" + URLEncoder.encode(user.getEmail(),
                            "UTF-8") + "&fullName=" +
                            URLEncoder.encode(dbUser.getFullName(),
                                    "UTF-8") + "&uuid=" +
                            URLEncoder.encode(dbUser.getUuid().toString(),
                                    "UTF-8")
                            + "&phoneNumber=" +
                            URLEncoder.encode((phoneNumber != null) ?
                                    phoneNumber : "", "UTF-8") +
                            "&defaultLandingPage"
                            + URLEncoder.encode((landingPage != null) ?
                            landingPage : "", "UTF-8") + "&isMpinCreated="
                            + isMpinCreated;
        }


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

