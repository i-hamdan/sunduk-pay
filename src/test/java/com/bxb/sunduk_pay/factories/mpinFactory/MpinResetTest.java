package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.encryption.MpinEncryption;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.InvalidMpinException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) // IMPORTANT
class MpinResetTest {
    @Mock
    private MpinEncryption mpinEncryption;


    @Mock
    private MpinRepository repository;

    @Mock
    private Validations validations;

    @Mock
    private MpinValidations mpinValidations;

    @InjectMocks
    private MpinReset service;


    @Test
    void testGetMpinRequestType_ShouldReturnResetMpin() {
        // Act
        MpinRequestType type = service.getMpinRequestType();

        // Assert
        assertEquals(MpinRequestType.RESET_MPIN, type,
                "ResetMpin should return RESET-MPIN request type");
    }


    @Test
    void TestMpinResetSuccess() {

        MpinRequest request = MpinRequest.builder()
                .uuid("c876c669-229c-4808-8155-02b731741060")
                .mpin("5555")
                .newMpin("1234")
                .build();

        Mpin mpin = new Mpin();
        mpin.setMpin("OLD_ENCRYPTED");

        when(mpinValidations.findMpinByUuid(anyString())).thenReturn(mpin);


        when(mpinEncryption.encryptMpin("1234")).thenReturn("ENCRYPTED_1234");

        MpinResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("MPIN Change successfully.", response.getTitle());
        assertTrue(response.getMessage().contains("new MPIN"));
        verify(repository, times(1)).save(any());
    }

    // testing user validatios
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

    // testing invalid mpin validation
    @Test
    void testInvalidOldMpin() {
        MpinRequest request = MpinRequest.builder()
                .uuid("AA12")
                .mpin("0000")
                .newMpin("7890")
                .build();

        Mpin mpin = new Mpin();
        when(mpinValidations.findMpinByUuid(anyString())).thenReturn(mpin);

        doThrow(new InvalidMpinException("Invalid old MPIN"))
                .when(mpinValidations).validateMpin(anyString(), anyString());

        assertThrows(InvalidMpinException.class,
                () -> service.perform(request));

        verify(repository, never()).save(any());
    }
}