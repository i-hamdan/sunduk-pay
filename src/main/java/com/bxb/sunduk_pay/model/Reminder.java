package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.Duration;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * localDateTime associated with the reminder.
     */
    private LocalDateTime localDateTime;
    /**
     * isPaid associated with the reminder.
     */
    private Boolean isPaid;
    /**
     * DATE associated with the reminder.
     */
    private LocalDate date;
    /**
     * isAvailable associated with the reminder.
     */
    private Boolean isAvailable;

    /**
     * Indicates whether auto payment is enabled for this reminder.
     */
    private Boolean autoPayEnabled;

    /**
     * Indicates whether auto payment requires user confirmation.
     */
    private Boolean requiresConfirmation;

    /** Indicates whether an auto payment is currently
     *  in progress for this reminder. */
    @Column(name = "auto_pay_in_progress")
    private Boolean autoPayInProgress;

    /**
     * User associated with the reminder.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * One-to-many relationship with AutoPayConfirmation.
     */
    @OneToMany(mappedBy = "reminder",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<AutoPayConfirmation> confirmations;

}
