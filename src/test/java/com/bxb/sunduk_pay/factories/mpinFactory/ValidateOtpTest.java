package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.exception.InvalidMpinException;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateOtpTest {

    @Mock
    private Cache<String, String> otpCache;

    @InjectMocks
    private ValidateOtp validateOtp;

    @Test
    void testGetMpinRequestType_ShouldReturnValidateOtp() {
        MpinRequestType type = validateOtp.getMpinRequestType();

        assertEquals(MpinRequestType.VALIDATE_OTP, type,
                "ValidateOtp should return VALIDATE_OTP request type");
    }

    @Test
    void testValidateOtpSuccess() {

        MpinRequest request = MpinRequest.builder()
                .email("dddd@example.com")
                .otp("123456")
                .build();

        when(otpCache.getIfPresent("dddd@example.com")).thenReturn("123456");


        MpinResponse response = validateOtp.perform(request);


        assertNotNull(response);
        assertEquals("OTP validation successful.", response.getMessage());

        verify(otpCache, times(1)).invalidate("dddd@example.com");
    }

    @Test
    void testValidateOtpFailure_InvalidOtp() {
        MpinRequest request = MpinRequest.builder()
                .email("kyapata@test.com")
                .otp("111111")
                .build();

        when(otpCache.getIfPresent("kyapata@test.com")).thenReturn("222222");

        InvalidMpinException ex = assertThrows(
                InvalidMpinException.class,
                () -> validateOtp.perform(request)
        );

        assertTrue(ex.getMessage().contains("Invalid code"));
    }

    @Test
    void testValidateOtpFailure_NoOtpPresent() {
        MpinRequest request = MpinRequest.builder()
                .email("kyapata@test.com")
                .otp("999999")
                .build();

        when(otpCache.getIfPresent("kyapata@test.com")).thenReturn(null);

        InvalidMpinException ex = assertThrows(
                InvalidMpinException.class,
                () -> validateOtp.perform(request)
        );

        assertTrue(ex.getMessage().contains("Invalid code"));
    }

}
