package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.encryption.MpinEncryption;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.MpinAlreadyExists;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
        import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // IMPORTANT
class SetMpinServiceTest {

    @Mock
    private MpinEncryption mpinEncryption;

    @Mock
    private Validations validations;

    @Mock
    private MpinRepository repository;

    @Mock
    private MpinValidations mpinValidations;

    @InjectMocks
    private SetMpinService service;

    @Test
    void testSetMpinSuccess() {

        MpinRequest request = MpinRequest.builder()
                .uuid("c876c669-229c-4808-8155-02b731741060")
                .mpin("5555")
                .build();

        // Mocking user return from validations
        User user = new User();
        user.setUuid("c876c669-229c-4808-8155-02b731741060");

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(mpinEncryption.encryptMpin("5555")).thenReturn("ENCRYPTED");


        MpinResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("MPIN set successfully.", response.getTitle());
        assertTrue(response.getMessage().contains("MPIN"));

        verify(repository, times(1)).save(any());
    }


// testing mpinAlready exists validation
@Test
void testMpinAlreadyExists() {
    MpinRequest request = MpinRequest.builder()
            .uuid("112233")
            .mpin("5555")
            .build();

    User user = new User();
    user.setUuid("112233");

    when(validations.getUserInfo(anyString())).thenReturn(user);

    doThrow(new MpinAlreadyExists("MPIN already exists"))
            .when(mpinValidations).mpinIsExists(anyString());

    MpinAlreadyExists ex = assertThrows(MpinAlreadyExists.class,
            () -> service.perform(request));

    assertTrue(ex.getMessage().contains("exists"));
}
// testing user validatios
@Test
void testUserNotFound() {
    MpinRequest request = MpinRequest.builder()
            .uuid("112233")
            .mpin("5555")
            .build();

    when(validations.getUserInfo(anyString()))
            .thenThrow(new UserNotFoundException("User not found with UUID: "));

    UserNotFoundException ex = assertThrows(UserNotFoundException.class,
            () -> service.perform(request));

    assertTrue(ex.getMessage().contains("User"));
}




}
