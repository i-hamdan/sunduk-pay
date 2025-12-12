package com.bxb.sunduk_pay.photoFactory;

import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperation;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperationsFactory;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PhotoOperationsFactoryTest {


    @Mock
    private PhotoOperation uploadProfileOperation;

    @InjectMocks
    private PhotoOperationsFactory factory;

    @BeforeEach
    void setUp(){

        MockitoAnnotations.openMocks(this);

        // Mock behaviour define karna
        when(uploadProfileOperation.getPhotoRequestType())
                .thenReturn(PhotoRequestType.PROFILE_PHOTO);

        // Factory create karte waqt mocked operations list dete hain
        factory = new PhotoOperationsFactory(List.of(uploadProfileOperation));

        // @PostConstruct automatically JUnit me call nahi hota
        callInitMethod();

    }

    // init() ko manually call karna
    private void callInitMethod() {
        try {
            var method = PhotoOperationsFactory.class.getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testGetOpratins(){
        PhotoOperation po = factory.getOperation(PhotoRequestType.PROFILE_PHOTO);
        Assertions.assertNotNull(po);
        Assertions.assertEquals(uploadProfileOperation, po);
    }
}
