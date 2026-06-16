package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.exception.InvalidCredentialsException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import com.bxb.sunduk_pay.service.OtpService;
import com.bxb.sunduk_pay.service.UserService;
import com.bxb.sunduk_pay.factories.userFactory.UserOperation;
import com.bxb.sunduk_pay.factories.userFactory.UserOperationFactory;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.OtpChannel;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.util.UserStatus;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service implementation for user-related operations, including login,
 * signup, and user details retrieval. This class interacts with the
 * database, handles authentication sessions, and communicates with Kafka
 * for event-driven processing.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String LANDING_PAGE_KEY = "DEFAULT_LANDING_PAGE";

    private static final Double DEFAULT_AUTO_PAY_THRESHOLD = 50000d;

    /**
     * Factory for creating user operations.
     */
    private final UserOperationFactory userOperation;
    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;
    /**
     * Mapper for converting between User entities and DTOs.
     */
    private final UserMapper userMapper;
    /**
     * Kafka template for sending user events.
     */
    private final KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;
    /**
     * Repository for main wallet data access.
     */
    private final MainWalletRepository mainWalletRepository;
    /**
     * Repository for master wallet data access.
     */
    private final MasterWalletRepository masterWalletRepository;
    /**
     * Repository for MPIN data access.
     */
    private final MpinRepository mpinRepository;
    /**
     * Service for handling OTP operations.
     */
    private final OtpService otpService;
    /**
     * Kafka template for sending OTP events.
     */
    private final KafkaTemplate<String, OtpEvent> kafkaTemplate1;
    /**
     * Password encoder for hashing user passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Service for managing authenticated sessions.
     */
    private final AuthenticationSessionService sessionService;
    /**
     * HTTP response for setting cookies during signup/login.
     */
    private final HttpServletResponse httpServletResponse;
    /**
     * Repository for global landing page data access.
     */
    private final GlobalLandingPageRepository globalLandingPageRepository;

    /** Repository for notification preference data access. */
    private final NotificationPreferenceRepository preferenceRepository;

    private final Validations validations;


    /**
     * HTTP session for managing user sessions.
     */
    private final HttpSession httpSession;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public User userLogin(final UserResponse response) {

        Optional<User> userOptional
                = userRepository.findByEmailAndIsDeletedFalse(
                response.getEmail());

        User user;
        if (userOptional.isEmpty()) {
            log.info(
                    "User not found in DB. Creating " +
                            "new user for email: {}",
                    response.getEmail());

            AuthProvider authProvider = AuthProvider.builder()
                    .authProviderMethod(AuthProviderMethod.GOOGLE)
                    .build();
            Set<AuthProvider> authProviders = Set.of(authProvider);

            user = userMapper.toUser(response);
            user.setUuid(UUID.randomUUID().toString());
            user.setPhoneNumber(response.getPhoneNumber());
            user.setIsDeleted(false);
            user.setAutoPayThresholdAmount(DEFAULT_AUTO_PAY_THRESHOLD);
            user.setUserRole(UserRoles.NORMAL_USER);
            user.setUserStatus(UserStatus.ACTIVE);
            user.setAuthProviders(authProviders);

            authProvider.setUser(user);
            user = userRepository.save(user);

            MainWallet mainWallet = MainWallet.builder()
                    .mainWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            mainWallet = mainWalletRepository.save(mainWallet);

            MasterWallet masterWallet = MasterWallet.builder()
                    .masterWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .mainWallet(mainWallet)
                    .createdAt(LocalDateTime.now())
                    .build();
            masterWallet = masterWalletRepository.save(masterWallet);

            // Create and save notification preferences for the new user
            buildPreference(user);

            user.setMasterWallet(masterWallet);
            user.setMainWallet(mainWallet);
            userRepository.save(user);

            UserKafkaEvent userEvent = userMapper
                    .toKafkaEvent(user, "SIGNUP");

            log.info("Sending user signup event to Kafka for email: {}",
                    user.getEmail());

            kafkaTemplate.send("user-topic", userEvent);
            log.info("New user saved with UUID: {}", user.getUuid());


        } else {
            user = userOptional.get();
            if (user.getPhoneNumber() == null) {
                log.error("User found but phone number is " +
                                "null for email: {}",
                        user.getEmail());
            }
            UserKafkaEvent userEvent = userMapper
                    .toKafkaEvent(user, "LOGIN");
            log.info("Sending user login event to Kafka for email: {}",
                    user.getEmail());

            kafkaTemplate.send("user-topic", userEvent);
            log.info("Login successful");

        }

        boolean present = mpinRepository
                .findByUserUuid(user.getUuid()).isPresent();
        user.setIsMpinCreated(present);


        if(user.getPreferredLandingPage()!=null) {
            log.info("User has preferred landing page: {}",
                    user.getPreferredLandingPage());
        } else {
            log.info("User does not have preferred landing page."
                    + " Fetching default landing page.");
            user.setPreferredLandingPage(getDefaultLandingPage());
        }
        return user;
    }

    /**
     * Builds and saves the notification preference for a user.
     *
     * @param user The user for whom the notification preference is being built.
     */
    private void buildPreference(User user) {
        if (preferenceRepository.existsByUser(user)) {
            return;
        }

        NotificationPreference preference =
                NotificationPreference.builder()
                        .user(user)
                        .build();

        preferenceRepository.save(preference);
    }

    /**
     * Retrieves the default landing page URL from the database.
     *
     * @return The default landing page URL, or null if not found.
     */
    private String getDefaultLandingPage() {

        GlobalLandingPage globalLandingPage =
                globalLandingPageRepository
                        .findByKey(LANDING_PAGE_KEY)
                        .orElse(null);

        return globalLandingPage != null
                ? globalLandingPage.getValue()
                : null;
    }

    /**
     * Performs user operations based on the request type.
     *
     * @param request The user request containing operation details.
     * @return The user response after performing the operation.
     */
    @Override
    public UserResponse userOperations(final UserRequest request) {
        UserOperation userOperations =
                userOperation.getUserOperations(request.getUserRequestType());
        return userOperations.perform(request);
    }
}

