package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.exceptions.EmailSendingException;
import com.bxb.sunduk_pay.service.EmailService;
import com.bxb.sunduk_pay.util.EmailMessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending emails.
 * Handles user activity emails and goal completion notifications.
 */
@Service
@Log4j2
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailMessageUtil emailMessageUtil;

    /**
     * Constructs the EmailServiceImpl.
     *
     * @param mailSender        JavaMailSender for sending emails
     * @param emailMessageUtil  Utility class for building email subjects and bodies
     */
    public EmailServiceImpl(JavaMailSender mailSender, EmailMessageUtil emailMessageUtil) {
        this.mailSender = mailSender;
        this.emailMessageUtil = emailMessageUtil;
    }

    /**
     * Processes a user-related Kafka event and sends an email notification.
     *
     * @param event the user Kafka event containing email and event details
     */
    public void processEmailEvent(UserKafkaEvent event) {
        String subject = emailMessageUtil.buildSubject(event);
        String body = emailMessageUtil.buildBody(event);
        sendEmail(event.getEmail(), subject, body);
    }

    /**
     * Processes a goal completion event and sends an email notification.
     *
     * @param event the goal completion event containing email and goal details
     */
    @Override
    public void processGoalCompletionEvent(GoalCompletionEvent event) {
        String subject = emailMessageUtil.buildGoalSubject(event);
        String body = emailMessageUtil.buildGoalBody(event);
        sendEmail(event.getEmail(), subject, body);
    }

    /**
     * Sends an email with the specified recipient, subject, and body.
     *
     * @param to      recipient email address
     * @param subject subject of the email
     * @param body    body content of the email
     * @throws EmailSendingException if the email fails to send
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to);
            throw new EmailSendingException("Failed to send email to: " + to);
        }
    }
}
