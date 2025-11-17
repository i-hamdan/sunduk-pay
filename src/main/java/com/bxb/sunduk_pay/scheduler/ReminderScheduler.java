package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler component to send reminder notifications to users.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ReminderScheduler {
/**     * Repository for accessing reminders.
     */
    private final ReminderRepository reminderRepository;
    /**     * Service for sending push notifications.
     */
    private final PushNotificationService pushNotificationService;
/**
     * Scheduled method to send reminder notifications based on their duration.
     */
    @Scheduled(cron = "0 0 0 * * *") // runs every 24h
public void sendReminderNotifications() {
        LocalDate today = LocalDate.now();
        List<Reminder> remindersToSend = new ArrayList<>();

        // --- DAILY ---
        List<Reminder> dailyReminders = reminderRepository
                .findByDuration(Duration.DAILY);
        remindersToSend.addAll(dailyReminders);

        // --- WEEKLY ---
        List<Reminder> weeklyReminders = reminderRepository
                .findByDuration(Duration.WEEKLY);
        for (Reminder r : weeklyReminders) {
            if (r.getStartDate() != null
                    && r.getStartDate()
                    .getDayOfWeek() == today.getDayOfWeek()) {
                remindersToSend.add(r);
            }
        }

        // --- MONTHLY ---
        List<Reminder> monthlyReminders = reminderRepository
                .findByDuration(Duration.MONTHLY);
        for (Reminder r : monthlyReminders) {
            if (r.getStartDate() != null
                    && r.getStartDate().getDayOfMonth() == today
                    .getDayOfMonth()) {
                remindersToSend.add(r);
            }
        }

        // --- YEARLY ---
        List<Reminder> yearlyReminders = reminderRepository
                .findByDuration(Duration.YEARLY);
        for (Reminder r : yearlyReminders) {
            if (r.getStartDate() != null
                    && r.getStartDate().getMonth() == today.getMonth()
                    && r.getStartDate().getDayOfMonth() == today
                    .getDayOfMonth()) {
                remindersToSend.add(r);
            }
        }

        if (remindersToSend.isEmpty()) {
            log.info("No reminders to notify today.");
            return;
        }

        for (Reminder reminder : remindersToSend) {
            User user = reminder.getUser();
            if (user == null || user.getFcmToken() == null) {
                continue;
            }

            String title = "Reminder Alert";
            String message = String.format(
                    "You have a %s reminder for AED %.2f. Note: %s",
                    reminder.getDuration(),
                    reminder.getAmount(),
                    reminder.getRemark()
            );

            try {
                pushNotificationService.sendNotification(
                        user.getFcmToken(), title, message
                );
            } catch (Exception e) {
                log.error("Failed to send notification: {}",
                        e.getMessage());
            }
        }

        log.info("Sent {} reminder notifications.",
                remindersToSend.size());
    }
}
