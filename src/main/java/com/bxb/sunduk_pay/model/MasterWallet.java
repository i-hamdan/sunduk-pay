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
 * Represents the master wallet entity in the Sunduk Pay system.
 * <p>
 * A master wallet belongs to a user and links to a main wallet.
 * It tracks balance, creation time, and soft deletion status.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "master_wallet")
public class MasterWallet {

    /** Unique identifier for the master wallet. */
    @Id
    private String masterWalletId;

    /** Current balance of the master wallet. */
    private Double balance;

    /** Indicates whether the master wallet is marked as deleted (soft delete). */
    private Boolean isDeleted;

    /** Timestamp when the master wallet was created. */
    @Timestamp
    private LocalDateTime createdAt;

    /** Reference to the user who owns this master wallet. */
    @DBRef
    private User user;

    /** Reference to the main wallet associated with this master wallet. */
    @DBRef
    private MainWallet mainWallet;
}
