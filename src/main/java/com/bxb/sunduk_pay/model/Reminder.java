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

@Entity
@Table(name = "reminders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * Model representing a reminder entity.
 */
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
     * User associated with the reminder.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
