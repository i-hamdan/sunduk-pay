package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.AuthProvider;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class SignupComplete implements UserOperation {

    private final Validations validations;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MainWalletRepository mainWalletRepository;
    private final MasterWalletRepository masterWalletRepository;
    private final UserMapper userMapper;
    private final HttpServletResponse httpServletResponse;
    private final HttpSession httpSession;
    private final MpinRepository mpinRepository;
    private final AuthenticationSessionService sessionService;
    private final HttpServletRequest httpServletRequest;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.SIGNUP_COMPLETE;
    }

    @Override
    public UserResponse perform(UserRequest request) {

        Optional<User> userOptional =
                validations.getUserByPhoneNumberOrEmail(
                        request.getPhoneNumber(), request.getEmail());

        if (userOptional.isEmpty()) {
            log.error("user not found");
            throw new UserNotFoundException("User not found with provided "
                    + "phone or email");
        }
        User user = userOptional.get();


        boolean isGoogleUser =
                user.getAuthProviders().stream().anyMatch(authProvider -> authProvider.getAuthProviderMethod() == AuthProviderMethod.GOOGLE);

        if (isGoogleUser) {

            log.info("User is a Google user. Email: {}",
                    user.getEmail());

            AuthProvider authProvider =
                    AuthProvider.builder().authProviderMethod(
                            AuthProviderMethod.LOCAL_EMAIL).build();
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userMapper.toUserForm(user, request);
            user.getAuthProviders().add(authProvider);
            authProvider.setUser(user);
            userRepository.save(user);
        } else {

            user.setPassword(passwordEncoder.encode(request.getPassword()));
            User user1 = user;
            user = userMapper.toUserForm(user1, request);

            MainWallet mainWallet =
                    MainWallet.builder().mainWalletId(UUID.randomUUID()
                            .toString())
                            .balance(0d)
                            .user(user1)
                            .createdAt(LocalDateTime.now())
                            .build();
            mainWallet = mainWalletRepository.save(mainWallet);

            log.info("Main wallet created with ID: {}",
                    mainWallet.getMainWalletId());

            user.setMainWallet(mainWallet);

            log.info("Main wallet associated with user: {}",
                    user1.getEmail());


            MasterWallet masterWallet =
                    MasterWallet.builder().masterWalletId(UUID.randomUUID()
                            .toString())
                            .balance(0d)
                            .user(user1)
                            .mainWallet(mainWallet)
                            .createdAt(LocalDateTime.now())
                            .build();

            masterWallet = masterWalletRepository.save(masterWallet);

            log.info("Master wallet created with ID: {}",
                    masterWallet.getMasterWalletId());

            user.setMasterWallet(masterWallet);
            log.info("User details updated with main and" + " master " +
                    "wallet for user: {}", user1.getEmail());

            userRepository.save(user1);
        }

        // 1. UUID Cookie
        Cookie uuidCookie = new Cookie("user_uuid", user.getUuid());
        uuidCookie.setPath("/");
        uuidCookie.setSecure(true);    // Required for ngrok/HTTPS
        uuidCookie.setHttpOnly(false); // Must be false for React Native to
        // read it

        String encodedName = URLEncoder.encode(user.getFullName(),
                StandardCharsets.UTF_8);

        Cookie usernameCookie = new Cookie("username", encodedName);
        usernameCookie.setPath("/");
        usernameCookie.setSecure(true);
        usernameCookie.setHttpOnly(false);


        boolean isMpinCreated =
                mpinRepository.findByUserUuid(user.getUuid()).isPresent();
        // 2. MPIN Status Cookie
        String mpinStatus = String.valueOf(isMpinCreated);
        Cookie mpinCookie = new Cookie("is_mpin_created", mpinStatus);
        mpinCookie.setPath("/");
        mpinCookie.setSecure(true);
        mpinCookie.setHttpOnly(false);

        httpServletResponse.addCookie(uuidCookie);
        httpServletResponse.addCookie(usernameCookie);
        httpServletResponse.addCookie(mpinCookie);

        // Since no UserDetails, we use Email/Phone as the principal string
        String principal = (request.getEmail() != null) ? request.getEmail()
                : user.getPhoneNumber();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal,
                        null,
                        List.of(new SimpleGrantedAuthority("NORMAL_USER")));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        // Push context to SecurityContextHolder and the Servlet Session
        SecurityContextHolder.setContext(context);
        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", context);

        sessionService.saveSession(httpSession.getId(), user);


        return UserResponse.builder().message("User signup completed " +
                "successfully.").build();
    }
}
