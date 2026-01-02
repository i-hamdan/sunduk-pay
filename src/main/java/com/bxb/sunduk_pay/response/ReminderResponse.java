package com.bxb.sunduk_pay.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response class representing a reminder.
 */
@Data
@Builder
public class ReminderResponse {

    /** Unique identifier for the reminder. */
    private String reminderId;
    /** Amount associated with the reminder. */
    private Double amount;
    /** Duration of the reminder (e.g., DAILY, WEEKLY). */
    private String duration;
    /** Start date of the reminder. */
    private LocalDate startDate;
    /** Additional remarks for the reminder. */
    private String remark;
    /** Next due date for the reminder. */
    private long nextDue;
    /** Contact name associated with reminder. */
    private String contactName;
    /** LocalDateTime associated with reminder. */
    private LocalDateTime DateTime;
    /**
     * isAvailable associated with the reminder.
     */
    private Boolean isAvailable;
    
}
