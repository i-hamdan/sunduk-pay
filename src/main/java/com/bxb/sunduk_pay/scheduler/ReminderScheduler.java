package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.Duration;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler for sending reminder notifications
 * based on their duration (daily, weekly, monthly, yearly).
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ReminderScheduler {

/**     * Repository for accessing reminders.
     */
    private final ReminderRepository reminderRepository;
    /**     * Validations for findByUserUuid .
     */
    private final Validations validations;
    /**
     * * Service for sending push notifications.
     */
    private final PushNotificationService pushNotificationService;
/**
     * Scheduled method to send reminder notifications based on their duration.
     */
  @Scheduled(cron = "0 0 0 * * *") // runs every 24h
  //  @Scheduled(cron = "0 * * * * *") // runs every 1 min
    public void sendReminderNotifications() {

        // Get today's date and current date-time
        LocalDate today = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // List to collect reminders that need to be sent
        List<Reminder> remindersToSend = new ArrayList<>();

        // Fetch all DAILY reminders
        List<Reminder> dailyReminders = reminderRepository
                .findByDuration(Duration.DAILY);

        log.info("Fetched {} DAILY reminders from database", dailyReminders.size());

        // Fetch all DAILY reminders
        for (Reminder reminder : dailyReminders) {
            log.debug("Processing reminder with ID: {}", reminder.getReminderId());

            LocalDate minusDays1 = today.minusDays(1);

//              Update reminder if it is unpaid and scheduled for today
             if (reminder.getDate().isEqual(today) && !reminder.getIsPaid()) {
                reminder.setLocalDateTime(currentDateTime);

                // Collect reminder to send notification
                remindersToSend.add(reminder);

                log.debug("Updated unpaid reminder for today. Reminder ID: {}", reminder.getReminderId());


//                   Update reminder time if it is unpaid and from yesterday
            } else if (reminder.getDate().isEqual(minusDays1) && !reminder.getIsPaid()) {
                 reminder.setLocalDateTime(currentDateTime);

                 log.debug("Updated unpaid reminder from yesterday. Reminder ID: {}", reminder.getReminderId());

            }

//              Create a new DAILY reminder if yesterday's reminder is unpaid and available
            if (!reminder.getIsPaid() &&
                    reminder.getDate().isEqual(minusDays1) &&
                    reminder.getIsAvailable()) {

                log.info("Creating new DAILY reminder from unpaid yesterday reminder. Reminder ID: {}", reminder.getReminderId());

                User user = validations.getUserInfo(reminder.getUser().getUuid());
                Reminder reminder1 = Reminder.builder()
                        .amount(reminder.getAmount())
                        .startDate(reminder.getStartDate())
                        .duration(Duration.DAILY)
                        .remark(reminder.getRemark())
                        .contactNumber(reminder.getContactNumber())
                        .contactName(reminder.getContactName())
                        .user(user)
                        .date(today)
                        .isPaid(false)
                        .isAvailable(false)
                        .localDateTime(currentDateTime)
                        .build();
                reminderRepository.save(reminder1);
                log.info("New DAILY reminder created successfully for user: {}", user.getUuid());
            }

//              Delete reminder if:
//              It is paid and not available
//              OR unpaid, not available, and 2 days old
            LocalDate minusDays2 = today.minusDays(2);
            if (reminder.getIsPaid() && !reminder.getIsAvailable()||
            !reminder.getIsPaid() && !reminder.getIsAvailable()
                    && reminder.getDate().isEqual(minusDays2)) {
                reminderRepository.delete(reminder);

                log.warn("Deleted reminder due to expiration rules. Reminder ID: {}", reminder.getReminderId());
            }

//              Reset reminder if it is exactly 2 days old
            if (reminder.getDate().isEqual(minusDays2)) {
                reminder.setIsPaid(false);
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(today);

                log.info("Reset reminder date and payment status. Reminder ID: {}", reminder.getReminderId());
            }
            reminderRepository.save(reminder);

        }

        // Fetch all weekly reminders
        List<Reminder> weeklyReminders = reminderRepository
                .findByDuration(Duration.WEEKLY);
        log.debug("Total weekly reminders found: {}", weeklyReminders.size());

//          Process weekly reminder date adjustments
        for(Reminder reminder : weeklyReminders){

            LocalDate minusDays1 = today.minusDays(1);
            LocalDate minusDays2 = today.minusDays(2);
            LocalDate plusDays1 = today.plusDays(1);

            DayOfWeek dayOfWeek = reminder.getDate().getDayOfWeek();
            log.debug("Processing Reminder ID: {}, Reminder Day: {}",
                    reminder.getReminderId(), dayOfWeek);

//             Update reminder date and notification time
//              if reminder is unpaid and falls within -1, 0, or +1 day range
            if(minusDays1.getDayOfWeek().equals(dayOfWeek)&& !reminder.getIsPaid()){
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(minusDays1);
                log.info("Reminder ID {} updated to yesterday", reminder.getReminderId());

            } else if (today.getDayOfWeek().equals(dayOfWeek) && !reminder.getIsPaid()) {
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(today);

                // Collect reminder to send notification
                remindersToSend.add(reminder);
                log.info("Reminder ID {} updated to today", reminder.getReminderId());

            }else if(plusDays1.getDayOfWeek().equals(dayOfWeek) && !reminder.getIsPaid()){
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(plusDays1);
                log.info("Reminder ID {} updated to tomorrow", reminder.getReminderId());

            }

//              Clear notification time for reminders older than 2 days
            if(minusDays2.getDayOfWeek().equals(dayOfWeek) &&
            reminder.getIsAvailable()){

                reminder.setLocalDateTime(null);
                log.info("Cleared notification time for Reminder ID {}",
                        reminder.getReminderId());
            }

            DayOfWeek minusWeek = today.minusDays(6).getDayOfWeek();
            if (dayOfWeek.equals(minusWeek)) {
                reminder.setIsPaid(false);
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(today);
                log.info("Weekly reset applied for Reminder ID {}", reminder.getReminderId());
            }
            reminderRepository.save(reminder);
            log.debug("Reminder ID {} saved successfully", reminder.getReminderId());
        }
        log.info("Weekly reminder processing completed");


        // --- MONTHLY ---
        List<Reminder> monthlyReminders = reminderRepository
                .findByDuration(Duration.MONTHLY);
        log.debug("Total monthly reminders found: {}", monthlyReminders.size());

//          Process monthly reminder date adjustments
        for (Reminder reminder : monthlyReminders) {

            int reminderDay = reminder.getDate().getDayOfMonth();
            YearMonth currentMonth = YearMonth.from(today);

            // Safe day (28/29/30/31 handle)
            int validDay = Math.min(reminderDay, currentMonth.lengthOfMonth());
            LocalDate dueDate = currentMonth.atDay(validDay);

            //  If today is already after this month's window → move to next month
            if (today.isAfter(dueDate.plusDays(3))) {
                YearMonth nextMonth = currentMonth.plusMonths(1);

                validDay = Math.min(reminderDay, nextMonth.lengthOfMonth());
                dueDate = nextMonth.atDay(validDay);
                log.info("Reminder ID {} moved to next month. New due date: {}",
                        reminder.getReminderId(), dueDate);

            }else if (today.isBefore(dueDate.minusDays(3))){
                YearMonth previousMonth = currentMonth.minusMonths(1);

                validDay = Math.min(reminderDay, previousMonth.lengthOfMonth());
                dueDate = previousMonth.atDay(validDay);
                log.info("Reminder ID {} moved to previous month. New due date: {}",
                        reminder.getReminderId(), dueDate);
            }

//              Check if today's date is within the ±3 day window
//              and the reminder is unpaid & available
            for (int i = -3; i <= 3; i++) {
                LocalDate checkDate = today.plusDays(i);

                // Collect reminders to send notification
                if(checkDate.isEqual(today)){
                    remindersToSend.add(reminder);
                    log.debug("Reminder scheduled for sending. Reminder ID: {}, Date: {}",
                            reminder.getReminderId(), today);
                }

                if (checkDate.isEqual(dueDate)) {

                    reminder.setDate(checkDate);
                    reminder.setLocalDateTime(currentDateTime);

                    log.info("Monthly reminder triggered. Reminder ID: {}, Date: {}",
                            reminder.getReminderId(), checkDate);
                    break;
                }
            }

//             Optional: if you want to reset reminders that are too old
//              (more than 1 month ago) as unpaid
            if (today.isEqual(dueDate.minusMonths(1))) {
                reminder.setIsPaid(false);
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(today);
                log.info("Monthly reminder reset applied. Reminder ID: {}",
                        reminder.getReminderId());
            }

//             Clear notification time 4 days before due date
            if(today.isEqual(dueDate.minusDays(4))&&
            reminder.getIsAvailable()){
                reminder.setLocalDateTime(null);
                log.info("Cleared notification time for Monthly Reminder ID {}",
                        reminder.getReminderId());
            }

            reminderRepository.save(reminder);
            log.debug("Monthly reminder saved successfully. Reminder ID: {}",
                    reminder.getReminderId());
        }

        // --- YEARLY ---
        List<Reminder> yearlyReminders = reminderRepository
                .findByDuration(Duration.YEARLY);
        log.debug("Total yearly reminders found: {}", yearlyReminders.size());

//          Process yearly reminder date adjustments
        for (Reminder reminder : yearlyReminders) {
            LocalDate startDate = reminder.getDate();
            MonthDay monthDay = MonthDay.from(startDate);

//              Calculate due date for the current year
            LocalDate dueDate = monthDay.atYear(today.getYear());

//             If today is already after this year's ±7 day window → move to next year
            if (today.isAfter(dueDate.plusDays(7))) {
                dueDate = monthDay.atYear(today.getYear() + 1);
                log.info("Reminder ID {} moved to next year. New due date: {}",
                        reminder.getReminderId(), dueDate);

            }else if (today.isBefore(dueDate.minusDays(7))){
                dueDate = monthDay.atYear(today.getYear() -1);
                log.info("Reminder ID {} moved to previous year. New due date: {}",
                        reminder.getReminderId(), dueDate);
            }

//             Check if today's date is within the ±7 day window
//              and the reminder is unpaid & available
            for (int i = -7; i <= 7; i++) {
                LocalDate checkDate = today.plusDays(i);

                if(checkDate.isEqual(today)){
                    remindersToSend.add(reminder);
                }
                // Collect reminders to send notification
                if (checkDate.isEqual(dueDate)) {

                    reminder.setDate(checkDate);
                    reminder.setLocalDateTime(currentDateTime);

                    log.info("Yearly reminder triggered. Reminder ID: {}, Date: {}",
                            reminder.getReminderId(), checkDate);
                    break;
                }
            }

//              Reset yearly reminder if it is exactly one year old
            LocalDate oneYearAgo = today.minusYears(1);
            if (reminder.getDate().isEqual(oneYearAgo)) {
                reminder.setIsPaid(false);
                reminder.setLocalDateTime(currentDateTime);
                reminder.setDate(LocalDate.now());
                reminder.setDate(today);
                log.info("Yearly reminder reset applied. Reminder ID: {}",
                        reminder.getReminderId());
            }

//              Clear notification time 8 days after due date
            LocalDate minusDay8 = today.minusDays(8);
            if(reminder.getDate().isEqual(minusDay8) &&
                    reminder.getIsAvailable()){
                reminder.setLocalDateTime(null);
                log.info("Cleared notification time for Yearly Reminder ID {}",
                        reminder.getReminderId());
            }

            reminderRepository.save(reminder);
            log.debug("Yearly reminder saved successfully. Reminder ID: {}",
                    reminder.getReminderId());
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