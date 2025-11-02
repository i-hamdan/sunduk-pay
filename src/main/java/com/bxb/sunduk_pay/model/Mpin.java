package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a Mobile PIN (MPIN)
 * associated with a user.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mpin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * Unique identifier for the MPIN.
     */
    private Long pinId;
    /**
     * The user associated with this MPIN.
     */

    private String mpin;
    /**
     * Number of failed attempts to enter the MPIN.
     */
    private int failedAttempts;
    /**
     * Indicates whether the MPIN is locked due to too many failed attempts.
     */
    private boolean locked;
    /**
     * Timestamp until which the MPIN is locked.
     */
    private LocalDateTime lockedUntil;
    /**
     * The user associated with this MPIN.
     */
    @OneToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;
}

