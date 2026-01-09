package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.util.Duration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


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


/**     * Finds reminders by user UUID and contact number with pagination.
     * @param uuid the UUID of the user
     * @param contactNumber the contact number associated with the reminder
     * @param pageable pagination information
     * @return a list of reminders matching the specified user
 * UUID and contact number
     */
    List<Reminder> findByUserUuidAndContactNumber(
            String uuid, String contactNumber, Pageable pageable);
}
