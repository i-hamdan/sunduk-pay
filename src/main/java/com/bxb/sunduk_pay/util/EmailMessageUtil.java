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

    /** Constant for 50% milestone. */
    private static final int MILESTONE_HALF = 50;

    /** Constant for 75% milestone. */
    private static final int MILESTONE_THREE_QUARTERS = 75;

    /** Constant for 100% milestone. */
    private static final int MILESTONE_COMPLETE = 100;

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
     * Build the body text for a user event.
     *
     * @param event the user Kafka event
     * @return the email body
     */
    public String buildBody(final UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Assalamualaikum " + event.getFullName()
                    + ",\n\nWe're happy to see you back on Sunduk!\n"
                    + "You have successfully logged in to your account."
                    + "\n\nIf this wasn't you, please secure your account"
                    + " immediately.\n\nJazakAllah Khair,\nTeam Sunduk";
        } else {
            return "Assalamualaikum " + event.getFullName()
                    + ",\n\nWelcome to SundukPay! \n\nYour account has"
                    + " been successfully created, and you’re now part of"
                    + " a secure and seamless way to manage your money."
                    + "\n\nHere’s what you can do with SundukPay:\n"
                    + "• Add and manage funds with ease\n"
                    + "• Create Saving Pots to set goals and track progress\n"
                    + "• Deposit or withdraw money from your pots anytime\n"
                    + "• Transfer funds flexibly: pot ↔ wallet, pot ↔ pot\n"
                    + "• Make safe payments and monitor activity in real-time"
                    + "\n\nStart exploring today and take control of your"
                    + " finances like never before!\n\nIf you ever need help,"
                    + " our support team is ready.\n\nThank you for choosing"
                    + " SundukPay – we’re excited to see you achieve your"
                    + " financial goals!\n\nWarm regards,\nSundukPay Team";
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
            case MILESTONE_HALF ->
                    "🎯 You’re halfway to your savings goal, "
                            + event.getWalletName() + "!";
            case MILESTONE_THREE_QUARTERS ->
                    "💪 75% milestone reached in your savings goal!";
            case MILESTONE_COMPLETE ->
                    "🎉 Congratulations! You’ve achieved your savings goal!";
            default -> "Update on your savings goal";
        };
    }

    /**
     * Build the body text for a goal completion event.
     *
     * @param event the goal completion event
     * @return the email body
     */
    public String buildGoalBody(final GoalCompletionEvent event) {
        return switch (event.getMilestone()) {
            case MILESTONE_HALF -> "Hello,\n\nGreat progress! You’ve"
                    + " reached **50% of your savings goal** in *"
                    + event.getWalletName() + "*.\n\nYou’re halfway there"
                    + " — stay consistent and you’ll achieve your goal soon."
                    + "\n\nKeep it up!\n\nBest wishes,\nTeam Sunduk";

            case MILESTONE_THREE_QUARTERS -> "Hello,\n\nAmazing work!"
                    + " You’ve now reached **75% of your goal** in *"
                    + event.getWalletName() + "*.\n\nYou’re so close — just"
                    + " a little more effort and you’ll get there.\n\nStay"
                    + " motivated!\n\nCheers,\nTeam Sunduk";

            case MILESTONE_COMPLETE -> "Hello,\n\n🎉 Congratulations!"
                    + " You’ve achieved **100% of your savings goal** in *"
                    + event.getWalletName() + "*.\n\nThis is a fantastic"
                    + " accomplishment, and we’re proud to see your dedication"
                    + " paying off.\n\nHere’s to even bigger goals ahead!"
                    + "\n\nWith warm regards,\nTeam Sunduk";

            default -> "Hello,\n\nHere’s an update on your savings journey"
                    + " in *" + event.getWalletName() + "*.\n\nEvery step counts"
                    + " — keep moving forward and you’ll reach your goal.\n\n"
                    + "Best wishes,\nTeam Sunduk";
        };
    }
}
