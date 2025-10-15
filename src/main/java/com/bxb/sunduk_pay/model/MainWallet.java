package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the main wallet associated with a user.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainWallet {
    /**
     * Unique identifier for the main wallet.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String mainWalletId;
    /**
     * Current balance of the main wallet.
     */
    private Double balance;
    /**
     * main wallet creation timestamp.
     */
    private LocalDateTime createdAt;
    /**
     * main wallet last update timestamp.
     */
    private LocalDateTime updatedAt;
/**
 * User associated with the main wallet.
  */

    @OneToOne
    @JoinColumn(name = "user_uuid")
    private User user;
    /**
     * List of sub-wallets associated with the main wallet.
     */
    @OneToMany(mappedBy = "mainWallet")
    private List<SubWallet> subWallets = new ArrayList<>();
}