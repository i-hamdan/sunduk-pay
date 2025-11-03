package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.util.Duration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Reminder entities.
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, String> {

    /**
     * Finds reminders by duration type (DAILY, WEEKLY, etc.)
     * ignoring case.
     */
    List<Reminder> findByDuration(Duration duration);

    /**
     * Finds a reminder by its unique reminderId.
     */
    Reminder findByReminderId(String reminderId);
}
