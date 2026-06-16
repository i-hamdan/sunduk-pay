package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.util.FallbackEmailUtil;
import com.bxb.sunduk_pay.util.SmsMessageUtil;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.twilio.rest.api.v2010.account.Message;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmsServiceImplTest {

    @Mock
    private TwilioConfig twilioConfig;

    @Mock
    private SmsMessageUtil smsMessageUtil;

    @Mock
    private FallbackEmailUtil fallbackEmailUtil;

    @InjectMocks
    private SmsServiceImpl smsServiceImpl;

    @Test
    void processSmsEvent_smsSuccess_shouldNotSendFallBackEmail() {

        // Arrange
        TransactionEvent event = mock(TransactionEvent.class);

        when(event.getPhoneNumber()).thenReturn("+917408089772");
        when(event.getTransactionId()).thenReturn("Txt123");
        when(smsMessageUtil.buildTransactionSms(event))
                .thenReturn("Test SMS");

        // sendSms ko spy se mock karenge (kyunki ye isi class me hai)
        SmsServiceImpl spyService = Mockito.spy(smsServiceImpl);
            doNothing().when(spyService)
                .sendSms(anyString(), anyString());

        //Act
        spyService.processSmsEvent(event);

        // Assert / verify
        verify(spyService)
                .sendSms("+917408089772", "Test SMS");

        verify(fallbackEmailUtil, never())
                .sendFallbackTransactionEmail(any());

    }

    @Test
    void processSmsEvent_smsFailed_shouldSendFailBackEmail() {

        // Arrange
        TransactionEvent event = mock(TransactionEvent.class);


        when(event.getPhoneNumber()).thenReturn("+917408089772");
        when(event.getTransactionId()).thenReturn("Txt123");
        when(smsMessageUtil.buildTransactionSms(event))
                .thenReturn("Test SMS");

        SmsServiceImpl spyService = Mockito.spy(smsServiceImpl);

        doThrow(new SmsServiceException("SMS Failed"))
                .when(spyService)
                .sendSms(anyString(), anyString());

        //Act
        spyService.processSmsEvent(event);

        //verify
        verify(fallbackEmailUtil)
                .sendFallbackTransactionEmail(event);
    }

    @Test
    void sendSms_success_shouldNotThrowException() {

        when(twilioConfig.getFromNumber())
                .thenReturn("+917408089772");

        try (MockedStatic<Message> mockedMessage =
                     Mockito.mockStatic(Message.class)) {

            mockedMessage.when(() ->
                    Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            anyString()
                    )
            ).thenReturn(mock(MessageCreator.class));

            Assertions.assertDoesNotThrow(() ->
                    smsServiceImpl.sendSms("+917408089772", "Test SMS"));
        }
    }

    @Test
    void sendSms_twilioFailure_shouldTrowSmsServiceImplException() {

        when(twilioConfig.getFromNumber())
                .thenReturn("+917408089772");

        try (MockedStatic<Message> mockedMessage =
                     Mockito.mockStatic(Message.class)) {


            mockedMessage.when(() ->
                    Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            anyString()
                    )
            ).thenThrow(new RuntimeException("Twilio Down"));

            Assertions.assertThrows(SmsServiceException.class, () ->
                    smsServiceImpl.sendSms("+917408089772", "Test SMS"));
        }
    }
}
