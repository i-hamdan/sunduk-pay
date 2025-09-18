package com.bxb.sunduk_pay.model;

import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a main wallet in the system.
 * Contains balance, transactions, and associated sub-wallets.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainWallet {

    /**
     * Unique identifier for the main wallet.
     */
    @Id
    private String mainWalletId;

    /**
     * Current balance of the wallet.
     */
    private Double balance;

    /**
     * Timestamp of wallet creation.
     */
    @Timestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp of last wallet update.
     */
    @Timestamp
    private LocalDateTime updatedAt;

    /**
     * User who owns this wallet.
     */
    @DBRef
    private User user;

    /**
     * List of transactions associated with this wallet.
     */
    @DBRef
    private List<Transaction> transactionHistory = new ArrayList<>();

    /**
     * List of sub-wallets under this main wallet.
     */
    private List<SubWallet> subWallets = new ArrayList<>();
}
