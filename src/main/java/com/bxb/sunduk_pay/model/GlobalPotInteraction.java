package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.CaseCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** Entity representing user interactions with global pots.
 */
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

    /** Primary key ID */
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

    /** Creation timestamp */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Last update timestamp */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Number of contributions made by the user to this pot */
    @Builder.Default
    private long contributionCount = 0;

    /** Timestamp of the last contribution made by the user to this pot */
    private LocalDateTime lastContributionAt;

    /** Total amount contributed by the user to this pot */
    @Builder.Default
    private Double totalContributedAmount = 0.0;

    /** Number of messages posted by the user in this pot */
    private long messageCount;

    /** Timestamp of the last message posted by the user in this pot */
    private LocalDateTime lastMessageAt;

    /** Timestamp of the last interaction (visit, contribution, message) */
    private LocalDateTime lastInteractedAt;
}
