package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.Mappers.MpinMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenrateOtpTest {
    @Mock
    private MpinMapper mpinMapper;
    @Mock
    private KafkaTemplate<String, OtpEvent> kafkaTemplate;
    @Mock
    private MpinValidations mpinValidations;
    @Mock
    private Cache<String, String> otpCache;

    @InjectMocks
    private GenerateOtp service;


    @Test
    void testGetMpinRequestType_ShouldReturnOTP() {
        MpinRequestType type = service.getMpinRequestType();

        assertEquals(MpinRequestType.OTP, type,
                "GenerateOtp should return OTP request type");
    }

    @Test
    void TestGenerateOtpSuccess(){

        MpinRequest request = MpinRequest.builder()
                .email("kahnb374@gmail.com")
                .build();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUuid("UUID123");
        user.setFullName("Test User");

        when(mpinValidations.getUserEmailInfo(anyString()))
                .thenReturn(user);

        doNothing().when(otpCache).put(anyString(), anyString());

        OtpEvent event = OtpEvent.builder()
                .email(user.getEmail())
                .fullname(user.getFullName())
                .otp("1234")
                .build();

        when(mpinMapper.toOtpEvent(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(event);

        when(kafkaTemplate.send(anyString(), any(OtpEvent.class)))
                .thenReturn(null);

        MpinResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("A Verification code  has been sent to your registered email.", response.getMessage());

        verify(kafkaTemplate, times(1)).send(eq("otp-topic"), any(OtpEvent.class));
    }


    @Test
    void testingUserNotFoundWithEmail(){
    MpinRequest request = MpinRequest.builder()
                .email("kanbhhh")
                .build();

    when(mpinValidations.getUserEmailInfo(anyString()))
                .thenThrow(new UserNotFoundException("this email is not registered with us"));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.perform(request));

        assertTrue(ex.getMessage().contains("email"));



    }


}