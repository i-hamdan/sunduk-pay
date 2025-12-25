package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
/**
 * Represents the main wallet associated with a user.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "main_wallet")
public class MainWallet {
    /**
     * Unique identifier for the main wallet.
     */
    @Id
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
    @OneToMany(mappedBy = "mainWallet",cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SubWallet> subWallets = new ArrayList<>();
}
