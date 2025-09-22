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
 * MainWallet entity representing a user's main wallet.
 */

@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
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
     * Timestamp when the main wallet was created.
     */
    @Timestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp when the main wallet was last updated.
     */
    @Timestamp
    private LocalDateTime updatedAt;

    /**
     * Reference to the user who owns the main wallet.
     */
    @DBRef
    private User user;

    /**
     * List of transactions associated with the main wallet.
     */
    @DBRef
    private List<Transaction> transactionHistory = new ArrayList<>();

    /**
     * List of sub-wallets under the main wallet.
     */
    private List<SubWallet> subWallets = new ArrayList<>();
}
