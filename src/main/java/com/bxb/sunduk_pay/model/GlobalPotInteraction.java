package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.CaseCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "global_pot_interactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_pot",
                        columnNames = {"user_uuid", "global_pot_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_uuid", columnList = "user_uuid"),
                @Index(name = "idx_global_pot_id", columnList = "global_pot_id"),
                @Index(name = "idx_case_category", columnList = "case_category")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalPotInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who interacted with the pot */
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User uuid;

    /** Global pot ID */
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "global_pot_id", nullable = false)
    private GlobalPot globalPot;

    /** Category of the pot (copied for fast querying) */
    @Enumerated(EnumType.STRING)
    @Column(name = "case_category", nullable = false)
    private CaseCategory caseCategory;

    /** Number of times user visited this pot */
    @Builder.Default
    private int visitCount = 0;

    /** Total time spent on this pot (in seconds) */
    @Builder.Default
    private long totalTimeSpentInSeconds = 0;

    /** Last visit timestamp */
    private LocalDateTime lastVisitedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
