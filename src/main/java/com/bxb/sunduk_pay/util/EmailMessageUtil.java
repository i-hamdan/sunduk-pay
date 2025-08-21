package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageUtil {


    public String buildSubject(UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Login Alert - Welcome back to Sunduk, " + event.getFullName() + "!";
        } else {
            return "Welcome to Sunduk family " + event.getFullName() + "!";
        }
    }

    public String buildBody(UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Assalamualaikum " + event.getFullName() + ",\n\n" +
                    "We're happy to see you back on Sunduk!\n" +
                    "You have successfully logged in to your account.\n\n" +
                    "If this wasn't you, please secure your account immediately.\n\n" +
                    "JazakAllah Khair,\nTeam Sunduk";
        } else {
            return "Assalamualaikum " + event.getFullName() + ",\n\n" +
                    "Welcome to SundukPay! \n\n" +
                    "Your account has been successfully created and you’re now part of a secure and seamless way to manage your money.\n\n" +
                    "You can start exploring features such as:\n" +
                    "• Adding and managing funds easily\n" +
                    "• Making safe payments\n" +
                    "• Tracking all your wallet transactions in real-time\n\n" +
                    "If you ever need help, our support team is just a message away.\n\n" +
                    "Thank you for choosing SundukPay – we’re excited to have you onboard!\n\n" +
                    "Warm regards,\n" +
                    "SundukPay Team";
        }
    }

}

