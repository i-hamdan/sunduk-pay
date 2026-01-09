package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user who is blocked from participating
 * in a specific GlobalPot.
 * This entity maintains the relationship between a GlobalPot
 * and the users who are blocked from it, along with the timestamp
 * of when the block was applied.
 */
@Entity
@Table(
        name = "global_pot_blocked_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"global_pot_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalPotBlockedUser {

    /**    * Unique identifier for the blocked user entry.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**    * Reference to the global pot from which the user is blocked.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id", nullable = false)
    private GlobalPot globalPot;

    /**    * Reference to the blocked user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 /**    * Timestamp when the user was blocked from the global pot.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime blockedAt;
}
