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

/**
 * MasterWallet entity representing a user's master wallet.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
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
     * Indicates whether the master wallet is deleted.
     */
    private Boolean isDeleted;

    /**
     * Timestamp when the master wallet was created.
     */
    @Timestamp
    private LocalDateTime createdAt;

    /**
     * Reference to the user who owns this wallet.
     */
    @DBRef
    private User user;

    /**
     * Reference to the associated main wallet.
     */
    @DBRef
    private MainWallet mainWallet;
}
