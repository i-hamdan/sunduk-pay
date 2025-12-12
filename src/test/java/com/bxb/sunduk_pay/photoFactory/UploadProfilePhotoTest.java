package com.bxb.sunduk_pay.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.factories.photoFactory.UploadProfilePhoto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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


    @Test
    void getPhotoRequestType(){
        PhotoRequestType photoRequestType = uploadProfilePhoto.getPhotoRequestType();
        assertEquals(PhotoRequestType.PROFILE_PHOTO, photoRequestType);
    }


    void performTest() throws IOException {

        byte[] imageBytes = new byte[]{1,2,3,4};
        when(multipartFile.getBytes()).thenReturn(imageBytes);


        User user = new User();
        user.setUuid(TEST_UUID);
        when(validations.getUserInfo(TEST_UUID)).thenReturn(user);

        // validations.validatePorfilePhoto should do nothing (no exception)
        doNothing().when(validations).validatePorfilePhoto(multipartFile);

        PhotoResponse

    }







}



