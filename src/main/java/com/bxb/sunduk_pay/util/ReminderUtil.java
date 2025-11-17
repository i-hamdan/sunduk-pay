package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.model.Reminder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for Reminder-related calculations.
 */
public class ReminderUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    // Prevent instantiation — required by Checkstyle
    private ReminderUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    /**
     * Calculates how many days remain until the next due reminder.
     * Returns 0 if the reminder is due today.
     * @param reminder the reminder object
     * @return number of days until the next due reminder
     */
    public static long calculateDaysUntilNextDue(final Reminder reminder) {
        if (reminder == null || reminder.getStartDate() == null
                || reminder.getDuration() == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextDueDate = reminder.getStartDate();

        // Move nextDueDate forward until it’s after today
        while (!nextDueDate.isAfter(today)) {
            nextDueDate = switch (reminder.getDuration().toString()) {
                case "DAILY" -> nextDueDate.plusDays(1);
                case "WEEKLY" -> nextDueDate.plusWeeks(1);
                case "MONTHLY" -> nextDueDate.plusMonths(1);
                case "YEARLY" -> nextDueDate.plusYears(1);
                default -> nextDueDate;
            };
        }

        // Calculate days between today and next due date
        return ChronoUnit.DAYS.between(today, nextDueDate);
    }
}
