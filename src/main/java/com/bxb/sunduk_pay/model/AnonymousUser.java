package com.bxb.sunduk_pay.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Represents an anonymous user within a global pot.
 * Each anonymous user is associated with a real user
 * and has a unique color code
 * within the context of a specific global pot.
 */
@Entity
@Table(
        name = "anonymous_user",
        uniqueConstraints = {
          @UniqueConstraint(columnNames = {"global_pot_id", "color_code"}),
          @UniqueConstraint(columnNames = {"global_pot_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnonymousUser {

    /**
     * Unique identifier for the anonymous user.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "anonymousId")
    private String anonymousId;

    /**
     * The global pot in which this anonymous identity exists.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "global_pot_id", nullable = false)
    private GlobalPot globalPot;

    /**
     * The real user behind the anonymous identity.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * This color IS the anonymous identity inside a pot.
     */
    @Column(name = "color_code", nullable = false)
    private String colorCode;
}

