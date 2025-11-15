package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.util.Duration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;


import java.util.List;

/**
 * Repository interface for managing Reminder entities.
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, String> {

    /**
     * Finds reminders by their duration.
     * @param duration the duration to filter reminders by
     * @return a list of reminders matching the specified duration
     */
    List<Reminder> findByDuration(Duration duration);

    /**
     * Finds a reminder by its unique reminderId.
     * @param reminderId the unique identifier of the reminder
     * @return the reminder with the specified reminderId
     */
    Reminder findByReminderId(String reminderId);

    /**
     * Finds reminders by contact number with pagination support.
     * @param contactNumber the contact number to filter reminders by
     * @param pageable the pagination information
     * @return a page of reminders matching the specified contact number
     */
    Page<Reminder> findByContactNumber(String contactNumber, Pageable pageable);

}
