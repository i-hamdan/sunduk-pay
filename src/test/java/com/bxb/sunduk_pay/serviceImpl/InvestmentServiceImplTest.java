package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.InvestmentFactory.InvestmentOperation;
import com.bxb.sunduk_pay.factories.InvestmentFactory.InvestmentOperationsFactory;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceImplTest {

    @Mock
    private InvestmentOperationsFactory factory;

    @Mock
    private InvestmentOperation investmentOperation;

    @InjectMocks
    private InvestmentServiceImpl investmentServiceImpl;


    @Test
    void investmentApi_shouldReturnResponseFromOperation(){

        InvestmentRequest request = new InvestmentRequest();
        request.setRequestType(InvestmentRequestType.CREATE_INVESTMENT);

        InvestmentResponse expectedResponse = new InvestmentResponse();

        // mock behaviour
        when(factory.getOperation(InvestmentRequestType.CREATE_INVESTMENT))
                .thenReturn(investmentOperation);

        when(investmentOperation.perform(request))
                .thenReturn(expectedResponse);

        // method call
        InvestmentResponse actualResponse =
                investmentServiceImpl.investmentApi(request);

        //Assert
        Assertions.assertEquals(expectedResponse,actualResponse);


        //verify
        verify(factory).getOperation(InvestmentRequestType.CREATE_INVESTMENT);
        verify(investmentOperation).perform(request);
    }
}
