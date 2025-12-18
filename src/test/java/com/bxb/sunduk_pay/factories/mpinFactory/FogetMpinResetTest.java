package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.encryption.MpinEncryption;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FogetMpinResetTest {
 @Mock
 private  MpinValidations mpinValidations;
 @Mock
 private  MpinEncryption mpinEncryption;
 @Mock
 private  MpinRepository repository;

 @InjectMocks
    private FogetMpinReset service;

    @Test
    void testGetForgotMpinRequestType_ShouldReturnForgotMpinReset() {
        MpinRequestType type = service.getMpinRequestType();

        assertEquals(MpinRequestType.FORGOT_MPIN_RESET, type,
                "ForgotMpinReset should return FORGOT_MPIN_RESET request type");
    }

    @Test
    void testForgotMpinResetSuccess() {

        MpinRequest request = MpinRequest.builder()
                .uuid("AA12")
                .mpin("0000")
                .newMpin("7890")
                .build();

        Mpin mpin = new Mpin();
        mpin.setMpin("5555");

        when(mpinValidations.findMpinByUuid(anyString())).thenReturn(mpin);

        when(mpinEncryption.encryptMpin("7890")).thenReturn("ENCRYPTED_7890");

        MpinResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("MPIN reset successfully.", response.getTitle());
        assertTrue(response.getMessage().contains("access your account"));

        verify(repository, times(1)).save(any());
    }

    @Test
    void testMpinNotFound() {
        MpinRequest request = MpinRequest.builder()
                .uuid("112233")
                .mpin("5555")
                .build();

        when(mpinValidations.findMpinByUuid(anyString()))
                .thenThrow(new UserNotFoundException("MPIN not found for user"));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.perform(request));

        assertTrue(ex.getMessage().contains("MPIN"));
    }

}