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

    private static final int MILESTONE_50 = 50;
    private static final int MILESTONE_75 = 75;
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
                    + "If this wasn't you, please secure your account immediately.\n\n"
                    + "JazakAllah Khair,\nTeam Sunduk";
        } else {
            return "Assalamualaikum " + event.getFullName() + ",\n\n"
                    + "Welcome to SundukPay! \n\n"
                    + "Your account has been successfully created, and you’re now part "
                    + "of a secure and seamless way to manage your money.\n\n"
                    + "Here’s what you can do with SundukPay:\n"
                    + "• Add and manage funds with ease\n"
                    + "• Create **Saving Pots** to set goals and track your progress\n"
                    + "• Deposit or withdraw money from your pots anytime\n"
                    + "• Transfer funds flexibly: pot ↔ wallet, and even pot ↔ pot\n"
                    + "• Make safe payments and monitor all wallet activity in real-time\n\n"
                    + "Start exploring today and take control of your finances like never before!\n\n"
                    + "If you ever need assistance, our support team is always ready to help.\n\n"
                    + "Thank you for choosing SundukPay – we’re excited to see you achieve "
                    + "your financial goals with us!\n\n"
                    + "Warm regards,\n"
                    + "SundukPay Team";
        }
    }

    /**
     * Build the subject line for a goal completion event.
     *
     * @param event the goal completion event
     * @return the subject line
     */
    public String buildGoalSubject(final GoalCompletionEvent event) {
        return switch (event.getMilestone()) {
            case MILESTONE_50 -> "🎯 You’re halfway to your savings goal, "
                    + event.getWalletName() + "!";
            case MILESTONE_75 -> "💪 75% milestone reached in your savings goal!";
            case MILESTONE_100 -> "🎉 Congratulations! You’ve achieved your savings goal!";
            default -> "Update on your savings goal";
        };
    }

    /**
     * Build the body content for a goal completion event.
     *
     * @param event the goal completion event
     * @return the body content
     */
    public String buildGoalBody(final GoalCompletionEvent event) {
        return switch (event.getMilestone()) {
            case MILESTONE_50 -> "Hello,\n\n"
                    + "Great progress! You’ve reached **50% of your savings goal** in *"
                    + event.getWalletName() + "*.\n\n"
                    + "You’re halfway there — stay consistent, and you’ll achieve your goal in no time.\n\n"
                    + "Keep it up!\n\n"
                    + "Best wishes,\nTeam Sunduk";
            case MILESTONE_75 -> "Hello,\n\n"
                    + "Amazing work! You’ve now reached **75% of your goal** in *"
                    + event.getWalletName() + "*.\n\n"
                    + "You’re so close — just a little more effort and you’ll get there.\n\n"
                    + "Stay motivated!\n\n"
                    + "Cheers,\nTeam Sunduk";
            case MILESTONE_100 -> "Hello,\n\n"
                    + "🎉 Congratulations! You’ve successfully achieved **100% of your savings goal** in *"
                    + event.getWalletName() + "*.\n\n"
                    + "This is a fantastic accomplishment, and we’re proud to see your dedication paying off.\n\n"
                    + "Here’s to even bigger goals ahead!\n\n"
                    + "With warm regards,\nTeam Sunduk";
            default -> "Hello,\n\n"
                    + "Here’s an update on your savings journey in *"
                    + event.getWalletName() + "*.\n\n"
                    + "Every step counts — keep moving forward and you’ll reach your goal.\n\n"
                    + "Best wishes,\nTeam Sunduk";
        };
    }
}
