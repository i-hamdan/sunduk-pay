package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.model.Reminder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for Reminder-related calculations.
 */
public final class ReminderUtil {

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
        LocalDate dueDate = reminder.getDate();

        // If reminder is in the past and NOT repeating
        if (dueDate.isBefore(today)) {
            return ChronoUnit.DAYS.between(today, dueDate); // negative value
        }
//        if(reminder.getDuration().equals(Duration.WEEKLY)){
//            ChronoUnit.DAYS.between(today.minusDays(7),dueDate);
//        }



        return ChronoUnit.DAYS.between(today, dueDate);
    }

}
