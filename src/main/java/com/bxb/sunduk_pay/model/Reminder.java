package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.Duration;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * Model representing a reminder entity.
 */
@Entity
@Table(name = "reminders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Reminder {

    /**
     * Unique identifier for the reminder.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reminderId;
    /**
     * Identifier of the amount associated with the reminder.
     */
    private Double amount;
    /**
     * Duration of the reminder.
     */
    @Enumerated(EnumType.STRING)
    private Duration duration;
    /**
     * Start date of the reminder.
     */
    private LocalDate startDate;
    /**
     * Additional remarks for the reminder.
     */
    private String remark;
    /**
     * Contact number associated with the reminder.
     */
    private String contactNumber;
    /** Contact name associated with reminder. */
    private String contactName;
    /**
     * User associated with the reminder.
     */

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
