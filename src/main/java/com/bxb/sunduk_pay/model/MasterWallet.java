package com.bxb.sunduk_pay.model;


import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a master wallet associated with a user.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterWallet {
    /**
     * Unique identifier for the master wallet.
     */
    @Id
    private String masterWalletId;
/**
* Current balance of the master wallet.
     */
    private Double balance;
/**
     * Timestamp when the master wallet was created.
     */
    @Timestamp
    private LocalDateTime createdAt;
    /**
     * User associated with the master wallet.
     */
    @OneToOne
    @JoinColumn(name = "user_uuid")
    private User user;
    /**
     * main wallet associated with the master wallet.
     */
    @OneToOne
    @JoinColumn(name = "main_wallet_id")
    private MainWallet mainWallet;
}