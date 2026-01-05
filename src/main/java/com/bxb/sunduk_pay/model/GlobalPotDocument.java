package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class GlobalPotDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalPotDocumentId;

    private String documentHeading;

    private String documentTitle;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] document;

    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
