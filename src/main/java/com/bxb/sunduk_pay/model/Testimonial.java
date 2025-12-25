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
 * Entity representing a Testimonial associated with a Global Pot.
 *
 * A testimonial contains feedback or remarks provided by an author
 * related to a specific GlobalPot. It may include author details,
 * profession, optional custom information, and a profile image.
 * This entity also tracks whether the testimonial was added by
 * a Sunduk admin along with audit timestamps.
 */
@Entity
@Getter
@Setter
@Table(name = "testimonials")
public class Testimonial {

    /**
     * Unique identifier for the testimonial.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String testimonialId;

    /**
     * Reference to the GlobalPot associated with this testimonial.
     */
    @ManyToOne
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    /**
     * Detailed content or message of the testimonial.
     */
    private String details;

    /**
     * Name of the author who provided the testimonial.
     */
    private String authorName;

    /**
     * Profession or role of the testimonial author.
     */
    private String profession;

    /**
     * Custom field for storing additional or flexible information.
     */
    private String customField;

    /**
     * Profile image of the testimonial author.
     * Stored as a LONGBLOB and fetched lazily to improve performance.
     */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] profileImage;

    /**
     * Indicates whether the testimonial was added by a Sunduk admin.
     */
    private Boolean isSundukAdmin;

    /**
     * Timestamp when the testimonial record was created.
     * Automatically populated and not updated afterwards.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the testimonial record was last updated.
     * Automatically updated on each modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
