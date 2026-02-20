package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.ConfirmationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents an auto-pay confirmation entity in the system.
 */
@Entity
@Table(name = "auto_pay_confirmations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AutoPayConfirmation {

    /**
     * Unique identifier for the auto-pay confirmation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String autoPayConfirmationId;

    /**
     * Foreign key reference to Reminder.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;

    /**
     * Foreign key reference to User.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    /**
     * Receiver phone stored for snapshot (important).
     */
    private String receiverPhone;

    /**
     * Payment amount snapshot.
     */
    private Double amount;

    /**
     * Confirmation status.
     */
    @Enumerated(EnumType.STRING)
    private ConfirmationStatus status;

    /**
     * Expiry time for confirmation.
     */
    private LocalDateTime expiresAt;

    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;
}
