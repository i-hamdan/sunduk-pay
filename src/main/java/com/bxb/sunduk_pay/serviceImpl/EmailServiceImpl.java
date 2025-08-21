package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.exception.EmailSendingException;
import com.bxb.sunduk_pay.service.EmailService;
import com.bxb.sunduk_pay.util.EmailMessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@Log4j2
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final EmailMessageUtil emailMessageUtil;

    public EmailServiceImpl(JavaMailSender mailSender, EmailMessageUtil emailMessageUtil) {
        this.mailSender = mailSender;
        this.emailMessageUtil = emailMessageUtil;
    }


    public void processEmailEvent(UserKafkaEvent event) {
        String subject = emailMessageUtil.buildSubject(event);
        String body = emailMessageUtil.buildBody(event);
        sendEmail(event.getEmail(), subject, body);
    }

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
