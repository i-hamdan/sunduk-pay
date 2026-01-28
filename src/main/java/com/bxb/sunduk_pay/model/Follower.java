package com.bxb.sunduk_pay.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Follower of a Global Pot.
 *
 * A follower is a registered user who chooses to follow
 * a specific GlobalPot to receive updates or track its activity.
 * This entity maps the relationship between a user and a GlobalPot
 * along with audit timestamps.
 */
@Entity
@Table(name = "followers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Follower {

    /**
     * Unique identifier for the follower.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String followerId;

    /**
     * The user who is following the GlobalPot.
     * This association is mandatory.
     */
    @ManyToOne
    @JoinColumn(name = "user_follower_id", nullable = false)
    private User followerUser;

    /**
     * The GlobalPot being followed by the user.
     * Defined as a many-to-one relationship since multiple users
     * can follow the same GlobalPot.
     * Loaded lazily to improve performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id")
    @ToString.Exclude
    private GlobalPot globalPot;

    /**
     * Timestamp when the follower record was created.
     * Automatically populated and not updated afterwards.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the follower record was last updated.
     * Automatically updated on each modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
