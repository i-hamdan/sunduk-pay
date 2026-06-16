package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperation;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperationsFactory;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceImplTest {

    @Mock
    private PhotoOperationsFactory photoOperationsFactory;

    @Mock
    private PhotoOperation photoOperation;

    @InjectMocks
    private PhotoServiceImpl photoService;


    @Test
            void testUploadPhoto(){

        PhotoRequest request = PhotoRequest.builder().photoRequestType(PhotoRequestType.PROFILE_PHOTO)
                .build();

        PhotoResponse expectedResponse = PhotoResponse.builder()
                .message("Profile photo uploaded successfully").build();
        when(photoOperationsFactory.getOperation(PhotoRequestType.PROFILE_PHOTO))
                .thenReturn(photoOperation);
        when(photoOperation.perform(request)).thenReturn(expectedResponse);

        PhotoResponse actualResponse = photoService.uploadPhoto(request);

        assertNotNull(actualResponse);
        assertEquals("Profile photo uploaded successfully", actualResponse.getMessage());

        verify(photoOperationsFactory).getOperation(PhotoRequestType.PROFILE_PHOTO);
        verify(photoOperation).perform(request);
    }


    @Test
    void testNullOperationRequest(){

        PhotoRequest request = PhotoRequest.builder().photoRequestType(PhotoRequestType.PROFILE_PHOTO)
                .build();

        when(photoOperationsFactory.getOperation(PhotoRequestType.PROFILE_PHOTO)).thenReturn(null);

        InvalidPhotoException exception = assertThrows(InvalidPhotoException.class,
                ()-> photoService.uploadPhoto(request));

        assertEquals("Invalid photo request type provided.", exception.getMessage());

        verify(photoOperationsFactory).getOperation(PhotoRequestType.PROFILE_PHOTO);
        verifyNoInteractions(photoOperation);
    }

}
