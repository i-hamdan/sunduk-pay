package com.bxb.sunduk_pay.logModel;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document to log user activity.
 */
@Data
@Builder
@Document(collection = "user_activity_log")
public class UserActivityLog {

    @Id
    private String logId;

    private String uuid;
    private String email;
    private String fullName;
    private String action;
    private String description;

    /**
     * Timestamp of the logged activity.
     */
    private LocalDateTime localDateTime;
}
