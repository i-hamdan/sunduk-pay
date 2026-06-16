package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the notification preferences for a user in the system.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "notification_preferences",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_uuid")
        }
)
public class NotificationPreference {
    /**
     * Unique identifier for the notification preference.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationPreferenceId;

    /**
     * User associated with the notification preferences.
     */
    @OneToOne
    @JoinColumn(
            name = "user_uuid",
            nullable = false,
            unique = true
    )    private User user;

    /**
     * Indicates whether the user wants to receive wallet notifications.
     */
    @Column(name = "wallet_notifications", nullable = false)
    @Builder.Default
    private Boolean walletNotifications = true;

    /**
     * Indicates whether the user wants to receive pot reminders.
     */
    @Column(name = "pot_reminders", nullable = false)
    @Builder.Default
    private Boolean potReminders = true;

    /**
     * Indicates whether the user wants to receive schedule reminders.
     */
    @Column(name = "schedule_reminders", nullable = false)
    @Builder.Default
    private Boolean scheduleReminders = true;

    /**
     * Indicates whether the user wants to receive promotional notifications.
     */
    @Column(name = "promotional_notifications", nullable = false)
    @Builder.Default
    private Boolean promotionalNotifications = true;

    /**
     * Indicates whether the user wants to receive system alerts.
     */
    @Column(name = "system_alerts", nullable = false, updatable = false)
    @Builder.Default
    private Boolean systemAlerts = true;

    /**
     * Timestamp when the notification preferences were created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the notification preferences were last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to set default values,
     * including timestamps and system alerts, when the entity is persisted.
     */
    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.systemAlerts = true;
    }
}
