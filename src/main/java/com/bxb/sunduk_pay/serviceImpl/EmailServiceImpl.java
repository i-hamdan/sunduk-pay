package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.EmailSendingException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import com.bxb.sunduk_pay.util.EmailMessageUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending emails.
 * Handles user activity emails and goal completion notifications.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    /** JavaMailSender for sending emails. */
    private final JavaMailSender mailSender;
    /** Utility for building email subjects and bodies. */
    private final EmailMessageUtil emailMessageUtil;



    /**
     * Processes a user-related Kafka event and sends an email notification.
     *
     * @param event the user Kafka event containing email and event details.
     */
    public void processEmailEvent(final UserKafkaEvent event) {
        String subject = emailMessageUtil.buildSubject(event);
        String body = emailMessageUtil.buildBody(event);
        sendEmail(event.getEmail(), subject, body,false);
    }

    @Override
    public void processOtpEvent(OtpEvent event) {
    String subject = emailMessageUtil.buildSubjectForOtp(event);
    String body = emailMessageUtil.buildBodyForOtp(event);
    sendEmail(event.getEmail(), subject, body, true);
    }


    /**
     * Sends an email with the specified recipient, subject, and body.
     *
     * @param to      recipient email address
     * @param subject subject of the email
     * @param body    body content of the email
     * @throws EmailSendingException if the email fails to send
     * @throws EmailSendingException if sending fails.
     */
    public void sendEmail(final String to,
                          final String subject,
                          final String body,
                          final Boolean isHtml) {
        try {
            if (isHtml) {
                //  HTML email using MimeMessage
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper
                        (message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);

                ClassPathResource image = new ClassPathResource("static/sundukPayLogo.png");
                helper.addInline("logoImage", image);

                mailSender.send(message);
                log.info(" HTML email sent to: {}", to);
            }
            else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            }
            } catch (Exception e) {
            log.error("Failed to send email to: {}", to);
            throw new EmailSendingException("Failed to send email to: " + to);
        }
    }


}
