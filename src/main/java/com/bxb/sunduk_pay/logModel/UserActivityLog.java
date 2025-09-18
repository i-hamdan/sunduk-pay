/**
 * Contains MongoDB models related to logging user activities.
 */
package com.bxb.sunduk_pay.logModel;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document representing a log entry of user activity.
 */
@Data
@Builder
@Document(collection = "user_activity_log")
public class UserActivityLog {

    /** Unique identifier for the log entry. */
    @Id
    private String logId;

    /** UUID of the user associated with this activity. */
    private String uuid;

    /** Email of the user performing the action. */
    private String email;

    /** Full name of the user performing the action. */
    private String fullName;

    /** Type of action performed (e.g., login, signup). */
    private String action;

    /** Detailed description of the action performed. */
    private String description;

    /** Timestamp when the activity was logged. */
    private LocalDateTime localDateTime;
}
