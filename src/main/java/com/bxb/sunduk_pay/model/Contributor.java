package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Contributor in a Global Pot.
 *
 * A contributor can be a registered user or an anonymous participant
 * who contributes a certain amount to a GlobalPot.
 * This entity stores contributor details, contribution amount,
 * profile image, and audit timestamps.
 */
@Entity
@Table(name = "contributers")
@Getter
@Setter
public class Contributor {

    /**
     * Unique identifier for the contributor.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String contributorId;

    /**
     * Reference to the user who is contributing.
     * This will be null if the contributor is anonymous.
     */
    @ManyToOne
    @JoinColumn(name = "user_contributor_id")
    private User userContributor;

    /**
     * Reference to the GlobalPot to which the contribution is made.
     */
    @ManyToOne
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    /**
     * Display name of the contributor.
     * Can be a real name or a custom name for anonymous contributors.
     */
    private String name;

    /**
     * Profile image of the contributor.
     * Stored as a LONGBLOB and fetched lazily for performance optimization.
     */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] profileImage;

    /**
     * Total amount contributed by the contributor.
     */
    private Double amountContributed;

    /**
     * Indicates whether the contribution was made anonymously.
     */
    private Boolean isAnonymous;

    /**
     * Indicates whether the contributor is a registered user.
     */
    private Boolean isUser;

    /**
     * Timestamp when the contributor record was created.
     * Automatically set and not updated afterwards.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the contributor record was last modified.
     * Automatically updated on each change.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
