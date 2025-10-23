package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import org.springframework.stereotype.Component;

/**
 * Utility component for building email subjects and bodies
 * for different user and goal events.
 */
@Component
public class EmailMessageUtil {
    /** Milestone constants. */
    private static final int MILESTONE_50 = 50;
    /** Milestone constants. */
    private static final int MILESTONE_75 = 75;
    /** Milestone constants. */
    private static final int MILESTONE_100 = 100;

    /**
     * Build the subject line for a user event.
     *
     * @param event the user Kafka event
     * @return the subject line
     */
    public String buildSubject(final UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Login Alert - Welcome back to Sunduk, "
                    + event.getFullName() + "!";
        } else {
            return "Welcome to Sunduk family "
                    + event.getFullName() + "!";
        }
    }

    /**
     * Build the body content for a user event.
     *
     * @param event the user Kafka event
     * @return the body content
     */
    public String buildBody(final UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Assalamualaikum " + event.getFullName() + ",\n\n"
                     + "We're happy to see you back on Sunduk!\n"
                     + "You have successfully logged in to your account.\n\n"
                     + "If this wasn't you, please "
                     +  "secure your account immediately.\n\n"
                     + "Team Sunduk";
        } else {
            return "Assalamualaikum " + event.getFullName() + ",\n\n"
                    + "Welcome to SundukPay! \n\n"
                    + "Your account has been successfully created, "
                    + "and you’re now part of a secure and seamless way "
                    + "to manage your money.\n\n"
                    + "Here’s what you can do with SundukPay:\n"
                    + "• Add and manage funds with ease\n"
                    + "• Create Saving Pots to "
                    + "set goals and track your progress\n"
                    + "• Deposit or withdraw money from your pots anytime\n"
                    + "• Transfer funds flexibly : "
                    + "pot ↔ wallet, and even pot ↔ pot\n"
             + "• Make safe payments and monitor all wallet activity in "
                    + "real-time\n\n"
                    + "Start exploring today and "
                    + "take control of your finances like "
                    + "never before!\n\n"
                    + "If you ever need assistance "
                    + "our support team is always ready "
                    + "to help.\n\n"
                    + "Thank you for choosing SundukPay,\n "
                    + "We’re excited to see you "
                    + "achieve your financial goals with us!\n\n"
                    + "Warm regards,\n"
                    + "SundukPay Team";
        }
    }

}
