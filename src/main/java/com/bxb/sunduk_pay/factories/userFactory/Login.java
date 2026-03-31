package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.exception.InvalidCredentialsException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.util.UserStatus;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class Login implements UserOperation {

    private final Validations validations;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final AuthenticationSessionService sessionService;
    private final HttpSession httpSession;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.LOGIN;
    }

    @Override
    public UserResponse perform(UserRequest request) {
            Optional<User> userOptional =
                    validations.getUserByPhoneNumberOrEmail(
                            request.getPhoneNumber(), request.getEmail());

            if (userOptional.isEmpty()) {
                throw new UserNotFoundException("User not found with provided" +
                        " " + "email or phone number");
            }
            User user = userOptional.get();


            if (!UserStatus.ACTIVE.equals(user.getUserStatus())) {
                throw new RuntimeException("User account is not active. " +
                        "Please " + "complete signup.");
            }
            if (!passwordEncoder.matches(request.getPassword(),
                    user.getPassword())) {
                throw new InvalidCredentialsException("Please enter the " +
                        "correct " + "password");
            }
            ServletRequestAttributes attr =
                    (ServletRequestAttributes)
                            RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);

            Cookie uuidCookie = new Cookie("user_uuid", user.getUuid());
            uuidCookie.setPath("/");
            uuidCookie.setSecure(true);    // Required for ngrok/HTTPS
            uuidCookie.setHttpOnly(false);

            String encodedName = URLEncoder.encode(user.getFullName(),
                    StandardCharsets.UTF_8);

            Cookie usernameCookie = new Cookie("username", encodedName);
            usernameCookie.setPath("/");
            usernameCookie.setSecure(true);    // Required for ngrok/HTTPS
            usernameCookie.setHttpOnly(false);

        String sessionId = httpSession.getId();
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", sessionId);
        jsessionIdCookie.setPath("/");
        jsessionIdCookie.setSecure(true);
        jsessionIdCookie.setHttpOnly(false);

            httpServletResponse.addCookie(uuidCookie);
            httpServletResponse.addCookie(usernameCookie);
            httpServletResponse.addCookie(jsessionIdCookie);


            sessionService.saveSession(sessionId, user);

            return UserResponse.builder().message("Login successful").build();
    }
}

