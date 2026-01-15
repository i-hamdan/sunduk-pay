    package com.bxb.sunduk_pay.model;

    import com.bxb.sunduk_pay.util.UserRoles;
    import jakarta.persistence.Entity;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;
    import jakarta.persistence.UniqueConstraint;
    import lombok.Builder;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.FetchType;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.Enumerated;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Column;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.AllArgsConstructor;
    import lombok.Setter;
    import lombok.AccessLevel;
    import org.apache.commons.lang3.builder.ToStringExclude;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "global_pot_member",
            uniqueConstraints = {@UniqueConstraint(columnNames =
                    {"user_id_of_member","global_pot_id"})
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
        private User user;

        /**
         * The global pot in which user will be a member.
         * Defined as a many-to-one relationship since multiple users
         * can follow the same GlobalPot.
         * Loaded lazily to improve performance.
         */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "global_pot_id", nullable = false)
        @ToStringExclude
        private GlobalPot globalPot;

        /**
         * The role of the user within the GlobalPot.
         * Stored as a string representation of the UserRoles enum.
         */
        @Enumerated(EnumType.STRING)
        private UserRoles userRoles;

        /**
         * Indicates whether the member is blocked from participating.
         * in the GlobalPot.
         * Defaults to false (not blocked).
         */
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
