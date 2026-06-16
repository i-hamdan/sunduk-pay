package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.mpinFactory.MpinOperation;
import com.bxb.sunduk_pay.factories.mpinFactory.MpinOperationsFactory;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MpinServiceImplTest {

    @Mock
    private MpinOperationsFactory mpinOperationsFactory;

    @Mock
    private MpinOperation mpinOperation;

    @InjectMocks
    private MpinServiceImpl service;

    @Test
    void testMpinApi(){

        MpinRequest request = MpinRequest.builder().mpinRequestType(MpinRequestType.SET_MPIN)
                .build();

        MpinResponse expectedResponse = MpinResponse.builder().message("MPIN set successfully")
                .build();

        when(mpinOperationsFactory.getMpinOperation(MpinRequestType.SET_MPIN))
                .thenReturn(mpinOperation);

        when(mpinOperation.perform(request)).thenReturn(expectedResponse);

        MpinResponse actualResponse = service.mpinApi(request);

        assertNotNull(actualResponse);
        assertEquals("MPIN set successfully",actualResponse.getMessage());

        verify(mpinOperationsFactory).getMpinOperation(MpinRequestType.SET_MPIN);
        verify(mpinOperation).perform(request);


    }


}