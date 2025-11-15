package com.bxb.sunduk_pay.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a sub-wallet associated with a main wallet.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SubWallet {
  /**
     * Unique identifier for the sub-wallet.
     */
    @Id
    private String subWalletId;
    /**
     * Name of the sub-wallet.
     */
    private String subWalletName;
    /**
     * Current balance of the sub-wallet.
     */
    private Double balance;
    /**
     * Target balance for the sub-wallet.
     */
    private Double targetBalance;
   /**
     * Target date to achieve the target balance.
     */
    private LocalDate targetDate;
  /**
     * Icon representing the sub-wallet.
     */
    private String icon;
    /**
     * Creation timestamp for the sub-wallet.
     */
    private LocalDateTime createdAt;
   /**
     * Last update timestamp for the sub-wallet.
     */
    private LocalDateTime updatedAt;
    /**
     * Indicates if the sub-wallet is deleted.
     */
    private Boolean isDeleted;
    /**
     * Main wallet associated with the sub-wallet.
     */
    @ManyToOne
    @JoinColumn(name = "main_wallet_id")
    private MainWallet mainWallet;
}
