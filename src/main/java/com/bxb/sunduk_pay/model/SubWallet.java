package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.SubWalletType;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a sub-wallet entity that is part of the Sunduk Pay system.
 * <p>
 * A sub-wallet can be used to organize funds into categories or goals.
 * Each sub-wallet holds information about its balance, target goals,
 * and timestamps for auditing.
 * </p>
 */
@Data
@Builder
public class SubWallet {

    /**
     * Unique identifier for the sub-wallet.
     */
    @Id
    private String subWalletId;

    /**
     * The name of the sub-wallet (e.g., "Travel Fund", "Emergency Savings").
     */
    private String subWalletName;

    /**
     * Current balance available in the sub-wallet.
     */
    private Double balance;

    /**
     * Target balance that the user aims to achieve in this sub-wallet.
     */
    private Double targetBalance;

    /**
     * The date by which the user intends to reach the target balance.
     */
    private LocalDate targetDate;

    /**
     * Icon or visual representation associated with the sub-wallet.
     */
    private String icon;

    /**
     * The timestamp when the sub-wallet was created.
     */
    @Timestamp
    private LocalDateTime createdAt;

    /**
     * The timestamp when the sub-wallet was last updated.
     */
    @Timestamp
    private LocalDateTime updatedAt;

    /**
     * Indicates whether the sub-wallet is marked as deleted.
     * <p>
     * This allows soft deletion without removing the record
     * from the database.
     * </p>
     */
    private Boolean isDeleted;
}
