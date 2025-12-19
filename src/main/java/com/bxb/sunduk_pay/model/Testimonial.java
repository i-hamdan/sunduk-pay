package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "testimonials")
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String testimonialId;

    @ManyToOne
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    private String details;

    private String authorName;

    private String profession;

    private String customField;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] profileImage;

    private Boolean isSundukAdmin;

    /** Automatically captured timestamp of record creation. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated timestamp of the last modification. */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
