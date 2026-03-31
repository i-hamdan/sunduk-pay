package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.EmailSendingException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.util.EmailCategory;
import com.bxb.sunduk_pay.util.EmailMessageUtil;
import com.bxb.sunduk_pay.util.OtpPurpose;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailMessageUtil emailMessageUtil;

    @InjectMocks
    private EmailServiceImpl emailService;


    @Test
    void testProcessEmailEventForSecurity() {
        UserKafkaEvent event = new UserKafkaEvent(
                "123",
                "Mohd Gulwaiz",
                "test@sunduk.com",
                "LOGIN",
                EmailCategory.SECURITY
        );

        when(emailMessageUtil.buildSubject(event))
                .thenReturn("Login");
        when(emailMessageUtil.buildBody(event))
                .thenReturn("Welcome to SundukPay!");

        emailService.processEmailEvent(event);

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));

    }

    @Test
    void testProcessEmailEventForWelcome() {
        UserKafkaEvent event = new UserKafkaEvent(
                "123",
                "Mohd Gulwaiz",
                "test@sunduk.com",
                "LOGIN",
                EmailCategory.WELCOME
        );

        when(emailMessageUtil.buildSubject(event))
                .thenReturn("Login Alert");
        when(emailMessageUtil.buildBody(event))
                .thenReturn("Login Successful");

        emailService.processEmailEvent(event);

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));

    }

    @Test
    void testProcessOtpEvent() {

        OtpEvent event = OtpEvent.builder()
                .fullname("Mohd Gulwaiz")
                .email("otp@sunduk.com")
                .otp("1234")
                .emailCategory(EmailCategory.SECURITY)
                .build();

        when(emailMessageUtil.buildSubjectForOtp(OtpPurpose.SIGNUP))
                .thenReturn("OTP Subject");
        when(emailMessageUtil.buildMpinResetOtpBody(event))
                .thenReturn("<html>OTP BODY</html>");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        emailService.processOtpEvent(event);

        verify(mailSender, times(1))
                .send(mimeMessage);
    }

    @Test
    void testSendPlainText() {

        emailService.sendEmail(
                "toUser@sunduk.com",
                "welcome@sunduk.com",
                "Welcome",
                "Hello User",
                false
        );

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmailHtml() {

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        emailService.sendEmail(
                "toUser@sunduk.com",
                "from@sunduk.com",
                "OTP",
                "<html>OTP</html>",
                true
        );

        verify(mailSender, times(1))
                .send(mimeMessage);
    }

    @Test
    void testSendEmailException() {

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        EmailSendingException exception = assertThrows(
                EmailSendingException.class, () -> emailService.sendEmail(
                        "toUser@sunduk.com",
                        "from@sunduk.com",
                        "Subject",
                        "Body",
                        false
                )
        );

        assertEquals(
                "Failed to send email to: toUser@sunduk.com",
                exception.getMessage()
        );
    }

}