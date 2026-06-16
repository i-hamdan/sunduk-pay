package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.exception.InvalidCredentialsException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordTest {

    @Mock
    private Validations validations;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangePassword changePassword;

    private UserRequest request;
    private User mockUser;

    @BeforeEach
    void setUp() {
        request = new UserRequest();
        request.setUuid("user-123");
        request.setOldPassword("currentPass");
        request.setNewPassword("newPass");

        mockUser = new User();
        mockUser.setUuid("user-123");
        mockUser.setPassword("encodedCurrentPass");
    }

    @Test
    void perform() {

        // 1. Arrange
        when(validations.getUserInfo("user-123")).thenReturn(mockUser);
        // Mock that the old password matches
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(true);
        // Mock the encoding of the new password
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        // 2. Act
        UserResponse response = changePassword.perform(request);

        // 3. Assert
        assertNotNull(response);
        assertEquals("Password changed successfully.", response.getMessage());
        assertEquals("encodedNewPass", mockUser.getPassword());

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void perform_WrongOldPassword_ShouldThrowException() {
        // 1. Arrange
        when(validations.getUserInfo("user-123")).thenReturn(mockUser);
        // Mock that the password DOES NOT match
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(false);

        // 2. Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            changePassword.perform(request);
        });

        assertEquals("Invalid old password provided.", exception.getMessage());

        // Verify save was NEVER called
        verify(userRepository, never()).save(any(User.class));
    }
}