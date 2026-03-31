package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.exception.InvalidCredentialsException;
import com.bxb.sunduk_pay.factories.userFactory.UserOperation;
import com.bxb.sunduk_pay.factories.userFactory.UserOperationFactory;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import com.bxb.sunduk_pay.service.OtpService;
import com.bxb.sunduk_pay.service.UserService;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.util.UserStatus;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserOperationFactory userOperationFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private MasterWalletRepository masterWalletRepository;

    @Mock
    private MpinRepository mpinRepository;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;
    @Mock
    private HttpSession httpSession;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationSessionService sessionService;
    @Mock
    private Validations validations;

    @Mock
    private GlobalLandingPageRepository globalLandingPageRepository;

    private MockHttpServletRequest mockRequest;


    @BeforeEach
    void setUp() {
        // Mock the Web Context (needed for RequestContextHolder)
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // Clear Security Context to ensure a clean state
        SecurityContextHolder.clearContext();
    }


    @Test
    void shouldCreateNewUserWhenUserNotExists() {

        // Given

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("email");
        userResponse.setPhoneNumber("2020202020");

        User mappedUser = new User();
        mappedUser.setEmail("email");

        GlobalLandingPage defaultPage = GlobalLandingPage.builder()
                .id(1L)
                .key("DEFAULT_LANDING_PAGE")
                .value("DASHBOARD")
                .build();

        when(globalLandingPageRepository.findByKey(anyString())).thenReturn(Optional.of(defaultPage));

        UserKafkaEvent kafkaEvent = new UserKafkaEvent();

        when(userMapper.toKafkaEvent(any(User.class), eq("SIGNUP"))).thenReturn(kafkaEvent);

        when(userRepository.findByEmailAndIsDeletedFalse("email")).thenReturn(Optional.empty());

        when(userMapper.toUser(userResponse)).thenReturn(mappedUser);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));


        when(mainWalletRepository.save(any(MainWallet.class))).thenAnswer(i -> i.getArgument(0));

        when(masterWalletRepository.save(any(MasterWallet.class))).thenAnswer(i -> i.getArgument(0));

        when(mpinRepository.findByUserUuid(anyString())).thenReturn(Optional.empty());

        // when
        User user = userServiceImpl.userLogin(userResponse);


        // Then
        Assertions.assertNotNull(user);
        assertEquals("email", user.getEmail());

        verify(kafkaTemplate).send(eq("user-topic"), any(UserKafkaEvent.class));


        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void shouldLoginExistingUser() {

        // Give
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("email");

        User existingUser = new User();
        existingUser.setEmail("email");
        existingUser.setUuid("uuid");

        GlobalLandingPage defaultPage = GlobalLandingPage.builder()
                .id(1L)
                .key("DEFAULT_LANDING_PAGE")
                .value("DASHBOARD")
                .build();

        when(globalLandingPageRepository.findByKey(anyString())).thenReturn(Optional.of(defaultPage));

        when(userRepository.findByEmailAndIsDeletedFalse("email")).thenReturn(Optional.of(existingUser));

        when(mpinRepository.findByUserUuid("uuid")).thenReturn(Optional.ofNullable(null));

        when(userMapper.toKafkaEvent(any(User.class), eq("LOGIN"))).thenReturn(new UserKafkaEvent());

        // when

        User user = userServiceImpl.userLogin(userResponse);

        // Then

        Assertions.assertNotNull(user);
        assertEquals("email", user.getEmail());

        verify(kafkaTemplate).send(eq("user-topic"), any(UserKafkaEvent.class));


    }


    @Test
    void shouldExecuteCorrectUserOperation() {

        UserRequest request = new UserRequest();
        request.setUserRequestType(UserRequestType.UPDATE);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setMessage("Success");

        UserOperation mockUserOperation = mock(UserOperation.class);

        when(userOperationFactory.getUserOperations(UserRequestType.UPDATE)).thenReturn(mockUserOperation);

        when(mockUserOperation.perform(request)).thenReturn(expectedUserResponse);

        // when
        UserResponse actualResponse = userServiceImpl.userOperations(request);

        // than
        Assertions.assertNotNull(actualResponse);
        assertEquals(expectedUserResponse, actualResponse);

        verify(userOperationFactory).getUserOperations(UserRequestType.UPDATE);

        verify(mockUserOperation).perform(request);
    }

    @Test
    void shouldSendOtpOnPhone() {

        UserRequest request = new UserRequest();
        request.setPhoneNumber("9999999999");

        UserResponse response = userServiceImpl.initiateSignup(request);

        verify(otpService).sendOtp("9999999999", "SMS");

        assertEquals("OTP sent successfully to initiate signup", response.getMessage());
    }

    @Test
    void shouldSendOtpOnEmail() {

        UserRequest request = new UserRequest();
        request.setEmail("email@mail.com");

        UserResponse response = userServiceImpl.initiateSignup(request);

        verify(otpService).sendOtp("email@mail.com", "EMAIL");

        assertEquals("OTP sent successfully to initiate signup", response.getMessage());
    }

    @Test
    void shouldVerifyOtpSuccessfully() {

        UserRequest request = new UserRequest();

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setMessage("OTP verified");

        when(otpService.verifyOtp(request)).thenReturn(expectedResponse);

        UserResponse actual = userServiceImpl.verifyOtp(request);

        assertEquals(expectedResponse, actual);
    }

    @Test
    void login_InvalidPassword_ShouldThrowException() {
        UserRequest request = new UserRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setPassword("hashedPassword");
        user.setUserStatus(UserStatus.ACTIVE);

        when(validations.getUserByPhoneNumberOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userServiceImpl.login(request));
    }

    @Test
    void completeSignup_ManualUser_Success() {
        // 1. Arrange
        UserRequest request = new UserRequest();
        request.setEmail("user@example.com");
        request.setFullName("John Doe");
        request.setPassword("plainPassword");

        User existingUser = new User();
        existingUser.setUuid("uuid-1234");
        existingUser.setEmail("user@example.com"); // Added email here
        existingUser.setFullName("John Doe");
        existingUser.setAuthProviders(new HashSet<>());

        // Mocking dependencies
        when(validations.getUserByPhoneNumberOrEmail(any(), any())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // Check if your Service uses 'toUserForm'. If so, keep this:
        when(userMapper.toUserForm(any(), any())).thenReturn(existingUser);

        when(mpinRepository.findByUserUuid(anyString())).thenReturn(Optional.empty());

        when(mainWalletRepository.save(any(MainWallet.class))).thenAnswer(i -> i.getArgument(0));
        when(masterWalletRepository.save(any(MasterWallet.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // 2. Act
        UserResponse response = userServiceImpl.completeSignup(request);

        // 3. Assertions
        assertNotNull(response);
        assertEquals("User signup completed successfully.", response.getMessage());

        // Verify Interactions
        verify(userRepository).save(any(User.class));
        verify(mainWalletRepository).save(any(MainWallet.class));

        // Check if 3 cookies are indeed added (uuid, username, mpinStatus)
        verify(httpServletResponse, times(3)).addCookie(any(Cookie.class));

        // Verify Security Context
        // Note: This only works if completeSignup calls SecurityContextHolder.setContext(...)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should not be null");
        assertEquals("user@example.com", auth.getName());
    }

    @Test
    void userLogin_ExistingUser_Success() {
        // 1. Arrange
        UserResponse loginRequest = new UserResponse();
        loginRequest.setEmail("googleuser@gmail.com");
        loginRequest.setFullName("Google User");

        User existingUser = new User();
        existingUser.setUuid("uuid-999");
        existingUser.setEmail("googleuser@gmail.com");
        existingUser.setPhoneNumber("1234567890");
        existingUser.setIsMpinCreated(true);

        GlobalLandingPage defaultPage = GlobalLandingPage.builder()
                .id(1L)
                .key("DEFAULT_LANDING_PAGE")
                .value("DASHBOARD")
                .build();

        when(globalLandingPageRepository.findByKey(anyString())).thenReturn(Optional.of(defaultPage));

        // Mock: check if user exists in DB
        when(userRepository.findByEmailAndIsDeletedFalse("googleuser@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        // 2. Act
        User result = userServiceImpl.userLogin(loginRequest);

        // 3. Assert
        assertNotNull(result);
        assertEquals("uuid-999", result.getUuid());
        assertEquals("googleuser@gmail.com", result.getEmail());

        // Verify: No new user was saved because they already existed
        verify(userRepository, never()).save(any(User.class));
    }
}

