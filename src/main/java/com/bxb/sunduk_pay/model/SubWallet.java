package com.bxb.sunduk_pay.model;

import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing a SubWallet entity.
 */
@Data
@Builder
public class SubWallet {
    /**
     * The unique identifier for the sub-wallet.
     */
    @Id
    private String subWalletId;

    /**
     * The name of the sub-wallet.
     */
    private String subWalletName;

    /**
     * The current balance of the sub-wallet.
     */
    private Double balance;

    /**
     * The target balance to reach in the sub-wallet.
     */
    private Double targetBalance;

    /**
     * The target date by which to reach the target balance.
     */
    private LocalDate targetDate;

    /**
     * The icon representing the sub-wallet.
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
     * Indicates whether the sub-wallet is deleted.
     */
    private Boolean isDeleted;
}
