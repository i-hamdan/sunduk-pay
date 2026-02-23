package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.AutoPayConfirmationService;
import com.bxb.sunduk_pay.service.AutoPaymentService;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.Duration;
import com.bxb.sunduk_pay.validations.Validations;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.DayOfWeek;
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
    /**
     * Number of days for daily reminder lookback.
     */
    private static final int DAILY_LOOKBACK_DAYS = 1;
    /**
     * Number of days after which daily reminders expire.
     */
    private static final int DAILY_EXPIRY_DAYS = 2;
    /**
     * Number of days for weekly reminder lookback.
     */
    private static final int WEEKLY_LOOKBACK_DAYS = 1;
    /**
     * Number of days after which weekly reminders are reset.
     */
    private static final int WEEKLY_RESET_DAYS = 6;
    /**
     * Number of days for monthly reminder notification window.
     */
    private static final int MONTHLY_REMINDER_WINDOW_DAYS = 3;
    /**
     * Number of days after which monthly reminder notifications are cleared.
     */
    private static final int MONTHLY_NOTIFICATION_CLEAR_DAYS = 4;
    /**
     * Number of months after which monthly reminders are reset.
     */
    private static final int MONTHLY_RESET_MONTHS = 1;
    /**
     * Number of days for yearly reminder notification window.
     */
    private static final int YEARLY_REMINDER_WINDOW_DAYS = 7;
    /**
     * Number of days after which yearly reminder notifications are cleared.
     */
    private static final int YEARLY_NOTIFICATION_CLEAR_DAYS = 8;
    /**
     * Number of years after which yearly reminders are reset.
     */
    private static final int YEARLY_RESET_YEARS = 1;
    /**
     * Number of days before the yearly due date
     * when a reminder should be triggered.
     *
     * <p>
     * A negative value indicates days counted before
     * the scheduled due date.
     * </p>
     */
    private static final int YEARLY_BEFORE_DAYS_REMINDER = -7;
    /**
     * Number of days after the yearly due date
     * during which a reminder can still be triggered.
     *
     * <p>
     * A positive value indicates days counted after
     * the scheduled due date.
     * </p>
     */
    private static final int YEARLY_AFTER_DAYS_REMINDER = 7;
    /**
     * Number of days before the monthly due date
     * when a reminder should be triggered.
     */
    private static final int MONTHLY_BEFORE_DAYS_REMINDER = -3;
    /**
     * Number of days after the monthly due date
     * during which a reminder can still be triggered.
     */
    private static final int MONTHLY_AFTER_DAYS_REMINDER = 3;

    /**
     * Threshold amount above which auto-payment requires confirmation.
     */
    private static final int MINIMUM_AMOUNT_FOR_CONFIRMATION = 10000;
    /**
     *  Repository for accessing reminders.
     */
    private final ReminderRepository reminderRepository;
    /**     * Validations for findByUserUuid .
     */
    private final Validations validations;
    /**
     * * Service for sending push notifications.
     */
    private final PushNotificationService pushNotificationService;

    /** List to collect reminders that are eligible for auto-payment. */
    private final AutoPaymentService autoPaymentService;

    private final AutoPayConfirmationService autoPayConfirmationService;
    /**
    * List to collect reminders that need to be sent.
     */
    private List<Reminder> remindersToSend = new ArrayList<>();


    /**
     * Scheduled method to send reminder notifications based on their duration.
     */
//    @Scheduled(cron = "0 0 0 * * *") // runs every 24h
    @Scheduled(cron = "0 * * * * *") // runs every 1 min
//      @Scheduled(cron = "*/30 * * * * *") // runs every 30 seconds
    public void sendReminderNotifications() throws FirebaseMessagingException {
        remindersToSend.clear();
        dailyReminder();
        weeklyReminder();
        monthlyReminder();
        yearlyReminder();
        sendNotifications();
    }
    /**
     * Processes and schedules daily reminders.
     *
     * <p>
     * This method fetches all reminders configured with a DAILY duration
     * and prepares reminders that are due today to be sent as notifications.
     * </p>
     *
     * <p>
     * It also handles:
     * <ul>
     *     <li>Daily reminder validation</li>
     *     <li>Skipping paid or inactive reminders</li>
     *     <li>Preparing reminders for notification dispatch</li>
     * </ul>
     * </p>
     */
        public void dailyReminder() throws FirebaseMessagingException {
            LocalDate today = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Fetch all DAILY reminders
            List<Reminder> dailyReminders = reminderRepository
                    .findByDuration(Duration.DAILY);
            log.info("Fetched {} DAILY reminders from database",
                    dailyReminders.size());
            // Fetch all DAILY reminders
            for (Reminder reminder : dailyReminders) {
                log.debug("Processing reminder with ID: {}",
                        reminder.getReminderId());
                LocalDate minusDays1 = today.minusDays(DAILY_LOOKBACK_DAYS);

//              Update reminder if it is unpaid and scheduled for today
                if (reminder.getDate().isEqual(today)
                        && !reminder.getIsPaid()) {
                    reminder.setLocalDateTime(currentDateTime);
                    handleDueReminder(reminder);
                    log.debug("Updated unpaid reminder"
                                    + " for today. Reminder ID: {}",
                            reminder.getReminderId());

//                   Update reminder time if it is unpaid and from yesterday
                } else if (reminder.getDate().isEqual(minusDays1)
                        && !reminder.getIsPaid()) {
                    reminder.setLocalDateTime(currentDateTime);
                    log.debug("Updated unpaid reminder from"
                                    + " yesterday. Reminder ID: {}",
                            reminder.getReminderId());
                }

//  Create a new DAILY reminder if yesterday's reminder is unpaid and available
                if (!reminder.getIsPaid()
                        && reminder.getDate().isEqual(minusDays1)
                        && reminder.getIsAvailable()) {
                    log.info("Creating new DAILY reminder from"
                            + " unpaid yesterday reminder."
                            + " Reminder ID:"
                            + reminder.getReminderId());
                    User user = validations.getUserInfo(
                            reminder.getUser().getUuid());
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
                            .autoPayEnabled(reminder.getAutoPayEnabled())
                            .requiresConfirmation(
                                    reminder.getRequiresConfirmation())
                            .localDateTime(currentDateTime)
                            .build();
                    reminderRepository.save(reminder1);
                    log.info("New DAILY reminder created"
                                    + " successfully for user: {}",
                            user.getUuid());
                }

//              Delete reminder if:
//              It is paid and not available
//              OR unpaid, not available, and 2 days old
                LocalDate minusDays2 = today.minusDays(DAILY_EXPIRY_DAYS);
                if (reminder.getIsPaid() && !reminder.getIsAvailable()
                        || !reminder.getIsPaid() && !reminder.getIsAvailable()
                                && reminder.getDate().isEqual(minusDays2)) {
                    reminderRepository.delete(reminder);
                    log.warn("Deleted reminder due to expiration"
                                    + " rules. Reminder ID: {}",
                            reminder.getReminderId());
                }

//              Reset reminder if it is exactly 2 days old
                if (reminder.getDate().isEqual(minusDays2)) {
                    reminder.setIsPaid(false);
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(today);
                    log.info("Reset reminder date and payment"
                                    + " status. Reminder ID: {}",
                            reminder.getReminderId());
                }
                reminderRepository.save(reminder);
            }
        }
    /**
     * Processes and schedules weekly reminders.
     *
     * <p>
     * This method fetches all reminders configured with a WEEKLY duration,
     * evaluates their scheduled day of the week,
     * and prepares eligible reminders to be sent as notifications.
     * </p>
     *
     * <p>
     * It also handles:
     * <ul>
     *     <li>Weekly date alignment (previous, current, next day)</li>
     *     <li>Resetting unpaid reminders for a new week</li>
     *     <li>Clearing outdated notification timestamps</li>
     * </ul>
     * </p>
     */
        public void weeklyReminder() throws FirebaseMessagingException {

            LocalDate today = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();


            // Fetch all weekly reminders
            List<Reminder> weeklyReminders = reminderRepository
                    .findByDuration(Duration.WEEKLY);
            log.debug("Total weekly reminders found: {}",
                    weeklyReminders.size());

//          Process weekly reminder date adjustments
            for (Reminder reminder : weeklyReminders) {
                LocalDate minusDays1 = today.minusDays(WEEKLY_LOOKBACK_DAYS);
                LocalDate minusDays2 = today.minusDays(2);
                LocalDate plusDays1 = today.plusDays(1);
                DayOfWeek dayOfWeek = reminder.getDate().getDayOfWeek();
                log.debug("Processing Reminder ID: {}, Reminder Day: {}",
                        reminder.getReminderId(), dayOfWeek);

//             Update reminder date and notification time
//              if reminder is unpaid and falls within -1, 0, or +1 day range
                if (minusDays1.getDayOfWeek().equals(dayOfWeek)
                        && !reminder.getIsPaid()) {
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(minusDays1);
                    log.info("Reminder ID {} updated to yesterday",
                            reminder.getReminderId());
                } else if (today.getDayOfWeek().equals(dayOfWeek)
                        && !reminder.getIsPaid()) {
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(today);

                    handleDueReminder(reminder);
                    log.info("Reminder ID {} updated to today",
                            reminder.getReminderId());
                } else if (plusDays1.getDayOfWeek().equals(dayOfWeek)
                        && !reminder.getIsPaid()) {
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(plusDays1);
                    log.info("Reminder ID {} updated to tomorrow",
                            reminder.getReminderId());
                }

//              Clear notification time for reminders older than 2 days
                if (minusDays2.getDayOfWeek().equals(dayOfWeek)
                        && reminder.getIsAvailable()) {
                    reminder.setLocalDateTime(null);
                    log.info("Cleared notification time for Reminder ID {}",
                            reminder.getReminderId());
                }
                DayOfWeek minusWeek = today.minusDays(
                        WEEKLY_RESET_DAYS).getDayOfWeek();
                if (dayOfWeek.equals(minusWeek)) {
                    reminder.setIsPaid(false);
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(today);
                    log.info("Weekly reset applied for Reminder ID {}",
                            reminder.getReminderId());
                }
                reminderRepository.save(reminder);
                log.debug("Reminder ID {} saved successfully",
                        reminder.getReminderId());
            }
            log.info("Weekly reminder processing completed");
        }
    /**
     * Processes and schedules monthly reminders.
     *
     * <p>
     * This method fetches all reminders configured with a MONTHLY duration,
     * calculates their due dates for the current or next month,
     * and prepares eligible reminders to be sent as notifications.
     * </p>
     *
     * <p>
     * It also handles:
     * <ul>
     *     <li>Safe day-of-month calculation (28–31 day handling)</li>
     *     <li>Monthly reminder window validation</li>
     *     <li>Resetting unpaid reminders from previous months</li>
     *     <li>Clearing outdated notification timestamps</li>
     * </ul>
     * </p>
     */
        public void monthlyReminder() throws FirebaseMessagingException {

            LocalDate today = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();


            // --- MONTHLY ---
            List<Reminder> monthlyReminders = reminderRepository
                    .findByDuration(Duration.MONTHLY);
            log.debug("Total monthly reminders found: {}",
                    monthlyReminders.size());

//          Process monthly reminder date adjustments
            for (Reminder reminder : monthlyReminders) {
                int reminderDay = reminder.getDate().getDayOfMonth();
                YearMonth currentMonth = YearMonth.from(today);
                // Safe day (28/29/30/31 handle)
                int validDay = Math.min(reminderDay,
                        currentMonth.lengthOfMonth());
                LocalDate dueDate = currentMonth.atDay(validDay);
                //  If today is already after this month's window
                //  → move to next month
                if (today.isAfter(dueDate.plusDays(
                        MONTHLY_REMINDER_WINDOW_DAYS))) {
                    YearMonth nextMonth = currentMonth.plusMonths(1);
                    validDay = Math.min(reminderDay, nextMonth.lengthOfMonth());
                    dueDate = nextMonth.atDay(validDay);
                    log.info("Reminder ID {} moved to next month."
                                    + " New due date: {}",
                            reminder.getReminderId(), dueDate);
                } else if (today.isBefore(dueDate.minusDays(
                        MONTHLY_REMINDER_WINDOW_DAYS))) {
                    YearMonth previousMonth =
                            currentMonth.minusMonths(1);
                    validDay = Math.min(reminderDay,
                            previousMonth.lengthOfMonth());
                    dueDate = previousMonth.atDay(validDay);
                    log.info("Reminder ID {} moved to"
                                    + " previous month. New due date: {}",
                            reminder.getReminderId(), dueDate);
                }

//              Check if today's date is within the ±3 day window
//              and the reminder is unpaid & available
                for (int i = MONTHLY_BEFORE_DAYS_REMINDER;
                     i <= MONTHLY_AFTER_DAYS_REMINDER; i++) {
                    LocalDate checkDate = today.plusDays(i);
                    // Collect reminders to send notification
                    if (checkDate.isEqual(today)) {
                        handleDueReminder(reminder);
                        log.debug("Reminder scheduled for sending."
                                        + " Reminder ID: {}, Date: {}",
                                reminder.getReminderId(), today);
                    }
                    if (checkDate.isEqual(dueDate)) {
                        reminder.setDate(checkDate);
                        reminder.setLocalDateTime(currentDateTime);
                        log.info("Monthly reminder triggered."
                                        + " Reminder ID: {}, Date: {}",
                                reminder.getReminderId(), checkDate);
                        break;
                    }
                }

//             Optional: if you want to reset reminders that are too old
//              (more than 1 month ago) as unpaid
                if (today.isEqual(dueDate.minusMonths(MONTHLY_RESET_MONTHS))) {
                    reminder.setIsPaid(false);
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(today);
                    log.info("Monthly reminder reset applied. Reminder ID: {}",
                            reminder.getReminderId());
                }

//             Clear notification time 4 days before due date
                if (today.isEqual(dueDate.minusDays(
                        MONTHLY_NOTIFICATION_CLEAR_DAYS))
                        && reminder.getIsAvailable()) {
                    reminder.setLocalDateTime(null);
                    log.info("Cleared notification time for"
                                    + " Monthly Reminder ID {}",
                            reminder.getReminderId());
                }
                reminderRepository.save(reminder);
                log.debug("Monthly reminder saved successfully."
                                + " Reminder ID: {}",
                        reminder.getReminderId());
            }
        }
    /**
     * Processes and schedules yearly reminders.
     *
     * <p>
     * This method identifies all reminders configured with a YEARLY duration,
     * calculates their due dates based on the current year, and prepares
     * eligible reminders to be sent as notifications.
     * </p>
     *
     * <p>
     * It also handles:
     * <ul>
     *     <li>Yearly date adjustments</li>
     *     <li>Resetting unpaid reminders from previous years</li>
     *     <li>Clearing outdated notification timestamps</li>
     * </ul>
     * </p>
     */
        public void yearlyReminder() throws FirebaseMessagingException {

            LocalDate today = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();


            // --- YEARLY ---
            List<Reminder> yearlyReminders = reminderRepository
                    .findByDuration(Duration.YEARLY);
            log.debug("Total yearly reminders found: {}",
                    yearlyReminders.size());

//          Process yearly reminder date adjustments
            for (Reminder reminder : yearlyReminders) {
                LocalDate startDate = reminder.getDate();
                MonthDay monthDay = MonthDay.from(startDate);

//              Calculate due date for the current year
                LocalDate dueDate = monthDay.atYear(today.getYear());

//      If today is already after this year's ±7 day window → move to next year
                if (today.isAfter(dueDate.plusDays(
                        YEARLY_REMINDER_WINDOW_DAYS))) {
                    dueDate = monthDay.atYear(today.getYear() + 1);
                    log.info("Reminder ID {} moved to next year."
                                    + " New due date: {}",
                            reminder.getReminderId(), dueDate);
                } else if (today.isBefore(
                        dueDate.minusDays(YEARLY_REMINDER_WINDOW_DAYS))) {
                    dueDate = monthDay.atYear(today.getYear() - 1);
                    log.info("Reminder ID {} moved to previous year."
                                    + " New due date: {}",
                            reminder.getReminderId(), dueDate);
                }

//             Check if today's date is within the ±7 day window
//              and the reminder is unpaid & available
                for (int i = YEARLY_BEFORE_DAYS_REMINDER;
                     i <= YEARLY_AFTER_DAYS_REMINDER; i++) {
                    LocalDate checkDate = today.plusDays(i);
                    if (checkDate.isEqual(today)) {
                        handleDueReminder(reminder);
                        // Collect reminders to send notification
                    }
                    if (checkDate.isEqual(dueDate)) {
                        reminder.setDate(checkDate);
                        reminder.setLocalDateTime(currentDateTime);
                        log.info("Yearly reminder triggered."
                                        + " Reminder ID: {}, Date: {}",
                                reminder.getReminderId(), checkDate);
                        break;
                    }
                }

//              Reset yearly reminder if it is exactly one year old
                LocalDate oneYearAgo = today.minusYears(YEARLY_RESET_YEARS);
                if (reminder.getDate().isEqual(oneYearAgo)) {
                    reminder.setIsPaid(false);
                    reminder.setLocalDateTime(currentDateTime);
                    reminder.setDate(LocalDate.now());
                    reminder.setDate(today);
                    log.info("Yearly reminder reset applied."
                                    + " Reminder ID: {}",
                            reminder.getReminderId());
                }

//              Clear notification time 8 days after due date
                LocalDate minusDay8 =
                        today.minusDays(YEARLY_NOTIFICATION_CLEAR_DAYS);
                if (reminder.getDate().isEqual(minusDay8)
                        && reminder.getIsAvailable()) {
                    reminder.setLocalDateTime(null);
                    log.info("Cleared notification time for"
                                    + " Yearly Reminder ID {}",
                            reminder.getReminderId());
                }
                reminderRepository.save(reminder);
                log.debug("Yearly reminder saved successfully."
                                + " Reminder ID: {}",
                        reminder.getReminderId());
            }
        }
    /**
     * Sends push notifications for all scheduled reminders.
     *
     * <p>
     * This method iterates over the list of reminders to be sent and
     * sends a push notification to the associated user if a valid
     * FCM token is available.
     * </p>
     */
        public void sendNotifications() {
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

    /**
     * Handles due reminders that are eligible for auto-payment.
     *
     * <p>
     * This method processes reminders that have auto-pay enabled and
     * routes them for auto-payment processing. Reminders that are not
     * eligible for auto-payment are routed for notification instead.
     * </p>
     *
     * @param reminder the reminder to be processed for auto-payment
     */
    private void handleDueReminder(Reminder reminder) throws FirebaseMessagingException {

        if (!Boolean.TRUE.equals(reminder.getAutoPayEnabled())) {
            log.info("Reminder {} routed to normal notification",
                    reminder.getReminderId());
            remindersToSend.add(reminder);
            return;
        }

        Double amount = reminder.getAmount();
        Double threshold = reminder.getUser().getAutoPayThresholdAmount();

        log.info("Processing AUTOPAY decision for reminder {} amount {}",
                reminder.getReminderId(), amount);

        // ===== CASE 1: DIRECT AUTOPAY =====
        if (amount <= MINIMUM_AMOUNT_FOR_CONFIRMATION) {

            log.info("Amount <= 10k → Direct AUTOPAY");

            autoPaymentService.processAutoPayment(reminder);
            return;
        }

        // ===== CASE 2: REQUIRE CONFIRMATION =====
        if (amount > MINIMUM_AMOUNT_FOR_CONFIRMATION && amount <= threshold) {

            log.info("Amount between 10k and threshold → Creating confirmation");

            autoPayConfirmationService.createConfirmation(reminder);
            return;
        }

        // ===== CASE 3: EXCEEDS THRESHOLD =====
        if (amount > threshold) {

            log.warn("Amount exceeds threshold → AUTOPAY REJECTED");

            remindersToSend.add(reminder);
        }
    }


}
