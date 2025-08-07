package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.event.UserKafkaEvent;
import com.bxb.sunduk_pay.exception.EmailSendingException;
import com.bxb.sunduk_pay.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@Log4j2
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "user-topic", groupId = "email-service-group", concurrency = "3")
    public void consumeEmailEvent(UserKafkaEvent userKafkaEvent) {
        try {
            String subject;
            String body;

            if ("LOGIN".equalsIgnoreCase(userKafkaEvent.getEventType())) {
                subject = "Login Alert - Welcome back to Sunduk, " + userKafkaEvent.getFullName() + "!";
                body = "Assalamualaikum " + userKafkaEvent.getFullName() + ",\n\n" +
                        "We're happy to see you back on Sunduk!\n" +
                        "You have successfully logged in to your account.\n\n" +
                        "If this wasn't you, please secure your account immediately.\n\n" +
                        "JazakAllah Khair,\n" +
                        "Team Sunduk";
            } else {
                subject = "Welcome to Sunduk family " + userKafkaEvent.getFullName() + "!";
                body = "Assalamualaikum " + userKafkaEvent.getFullName() + ",\n\nThanks for registering with us!";
            }
            log.info("Preparing to send email to: {}", userKafkaEvent.getEmail());
            sendEmail(userKafkaEvent.getEmail(), subject, body);
            log.info("Email sent successfully to: {}", userKafkaEvent.getEmail());

        } catch (Exception e) {
            log.error("Error while processing email event for user: {}", userKafkaEvent.getEmail());
            throw new EmailSendingException("Failed to process email event for user: " + userKafkaEvent.getEmail());
        }
    }

@Async
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
