package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Global Wallet.
 *
 * A GlobalWallet is associated with a GlobalPot and is responsible
 * for maintaining the total balance of funds collected in that pot.
 * It also tracks the active status and audit timestamps.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalWallet {

    /**
     * Unique identifier for the global wallet.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalWalletId;

    /**
     * Current balance available in the global wallet.
     */
    private Double balance;

    /**
     * Indicates whether the global wallet is active or not.
     */
    private Boolean isActive;

    /**
     * Associated GlobalPot for this wallet.
     * This is the inverse side of a one-to-one relationship.
     */
    @OneToOne(mappedBy = "globalWallet")
    private GlobalPot globalPot;

    /**
     * Timestamp when the global wallet record was created.
     * Automatically populated and not updated afterwards.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the global wallet record was last modified.
     * Automatically updated on each change.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
