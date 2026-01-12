package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.UserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "global_pot_member",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id_of_member","global_pot_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GlobalPotMembers {

    /**
     * Unique identifier for member.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String memberId;

    /**
     * The user who is member of the GlobalPot.
     * This association is mandatory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_of_member", nullable = false)
    private User userId;

    /**
     * The global pot in which user will be a member.
     * Defined as a many-to-one relationship since multiple users
     * can follow the same GlobalPot.
     * Loaded lazily to improve performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id", nullable = false)
    @ToStringExclude
    private GlobalPot globalPotId;


    @Enumerated(EnumType.STRING)
    private UserRoles userRoles;


    private Boolean isBlocked = false;

    /**
     * Timestamp when the member record was created.
     * Automatically populated and not updated afterwards.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the member record was last updated.
     * Automatically updated on each modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}