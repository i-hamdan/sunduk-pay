package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a user activity log entry stored in MongoDB.
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLog {

    /**
     * Unique identifier for the log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String logId;

    /** UUID of the user associated with the activity.
     */
    private Long uuid;

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

    @ManyToOne
    @JoinColumn(name="user_uuid")
    private User user;

}
