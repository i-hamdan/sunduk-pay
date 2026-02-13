package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

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
@Table(name = "mpin",
        indexes = {
        @Index(name = "idx_mpin_user_uuid",
        columnList = "user_uuid")
        })
public class Mpin {
    /**
     * Unique identifier for the MPIN.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

