package com.bxb.sunduk_pay.factories.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import com.bxb.sunduk_pay.validations.Validations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UploadProfilePhotoTest {

    @Mock
    private Validations validations;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhotoRequest photoRequest;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UploadProfilePhoto uploadProfilePhoto;

    private final String TEST_UUID = "User-000";

    @BeforeEach
    void setUp() {
        when(photoRequest.getUuid()).thenReturn(TEST_UUID);
        when(photoRequest.getMultipartFile()).thenReturn(multipartFile);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
    }

    @Test
    void getPhotoRequestType() {
        PhotoRequestType photoRequestType = uploadProfilePhoto.getPhotoRequestType();
        assertEquals(PhotoRequestType.PROFILE_PHOTO, photoRequestType);
    }

    @Test
    void performTest() throws IOException {

        byte[] imageBytes = new byte[]{1, 2, 3, 4};
        when(multipartFile.getBytes()).thenReturn(imageBytes);

        User user = new User();
        user.setUuid(TEST_UUID);

        when(validations.getUserInfo(TEST_UUID)).thenReturn(user);
        doNothing().when(validations).validatePorfilePhoto(multipartFile);

        assertNotNull(photoRequest.getMultipartFile());
        PhotoResponse response = uploadProfilePhoto.perform(photoRequest);


        assertNotNull(response);
        assertTrue(response.getMessage().contains(TEST_UUID));
        assertArrayEquals(imageBytes, user.getProfilePhoto());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void perform_whenValidationFails_throwsInvalidPhotoException() throws Exception {

        doThrow(new RuntimeException("invalid file"))
                .when(validations)
                .validatePorfilePhoto(multipartFile);

        InvalidPhotoException ex = assertThrows(
                InvalidPhotoException.class,
                () -> uploadProfilePhoto.perform(photoRequest)
        );

        assertTrue(ex.getMessage().contains("invalid file"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void perform_whenGetBytesThrows_throwsInvalidPhotoException() throws Exception {

        when(multipartFile.getBytes()).thenThrow(new IOException("io error"));
        when(validations.getUserInfo(TEST_UUID)).thenReturn(new User());

        InvalidPhotoException ex = assertThrows(
                InvalidPhotoException.class,
                () -> uploadProfilePhoto.perform(photoRequest)
        );

        assertTrue(ex.getMessage().contains("io error"));
        verify(userRepository, never()).save(any());
    }
}