package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final PushNotificationService pushNotificationService;

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
            if (r.getStartDate() != null &&
                    r.getStartDate().getDayOfWeek() == today.getDayOfWeek()) {
                remindersToSend.add(r);
            }
        }

        // --- MONTHLY ---
        List<Reminder> monthlyReminders = reminderRepository
                .findByDuration(Duration.MONTHLY);
        for (Reminder r : monthlyReminders) {
            if (r.getStartDate() != null &&
                    r.getStartDate().getDayOfMonth() == today.getDayOfMonth()) {
                remindersToSend.add(r);
            }
        }

        // --- YEARLY ---
        List<Reminder> yearlyReminders = reminderRepository
                .findByDuration(Duration.YEARLY);
        for (Reminder r : yearlyReminders) {
            if (r.getStartDate() != null &&
                    r.getStartDate().getMonth() == today.getMonth() &&
                    r.getStartDate().getDayOfMonth() == today.getDayOfMonth()) {
                remindersToSend.add(r);
            }
        }

        if (remindersToSend.isEmpty()) {
            log.info("No reminders to notify today.");
            return;
        }

        for (Reminder reminder : remindersToSend) {
            User user = reminder.getUser();
            if (user == null || user.getFcmToken() == null) continue;

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
                log.error("Failed to send notification: {}", e.getMessage());
            }
        }

        log.info("Sent {} reminder notifications.", remindersToSend.size());
    }
}
