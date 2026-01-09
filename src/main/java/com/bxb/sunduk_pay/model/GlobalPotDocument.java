package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.DocumentStatus;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing a document associated with a GlobalPot.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class GlobalPotDocument {

    /** Unique identifier for the GlobalPotDocument.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalPotDocumentId;

    /** Heading of the document. */
    private String documentHeading;

    /** Title of the document. */
    private String documentTitle;

    /** The actual document content stored as a byte array. */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] document;

/** Status of the document (e.g., PENDING, APPROVED, REJECTED). */
    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus;

    /** Reference to the GlobalPot associated with this document. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    /** Timestamp of when the document was uploaded.
     * Automatically managed by Hibernate.
     */
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    /** Timestamp of the last update to the document record.
     * Automatically managed by Hibernate.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
