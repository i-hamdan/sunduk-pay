package com.bxb.sunduk_pay.logModel;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a user activity log entry stored in MongoDB.
 */
@Document
@Data
@Builder
public class UserActivityLog {

    /** Unique identifier for the log entry.
     */
    @Id
    private String logId;

    /** UUID of the user associated with the activity.
     */
    private String uuid;

    /** Email of the user associated with the activity.
     */
    private String email;

    /** Full name of the user associated with the activity.
     */
    private String fullName;

    /** Action performed by the user.
     */
    private String action;

    /** Description of the activity.
     */
    private String description;

    /** Timestamp of when the activity occurred.
     */
    private LocalDateTime localDateTime;
}
