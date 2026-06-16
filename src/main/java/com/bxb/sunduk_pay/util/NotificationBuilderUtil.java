package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.response.NotificationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/** * Utility class for building notification messages based on
 * different notification types.
 */
@Component
@Log4j2
public class NotificationBuilderUtil {

    public NotificationMessage build(
            final NotificationType type) {

        return switch (type) {

            case WALLET ->
                    new NotificationMessage(
                            "Wallet Update",
                            "Your wallet balance has changed.");

            case POT_REMINDER ->
                    new NotificationMessage(
                            "Pot Reminder",
                            "Your pot payment is due soon.");

            case SCHEDULE_REMINDER ->
                    new NotificationMessage(
                            "Schedule Reminder",
                            "You have an upcoming scheduled payment.");

            case PROMOTIONAL ->
                    new NotificationMessage(
                            "Special Offer",
                            "Check out our latest promotions!");

            case SYSTEM_ALERT ->
                    new NotificationMessage(
                            "System Alert",
                            "Important system notification.");
        };
    }

}