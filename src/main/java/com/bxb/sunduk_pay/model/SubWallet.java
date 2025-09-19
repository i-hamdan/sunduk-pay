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
    @Id
    private String subWalletId;
    private String subWalletName;
    private Double balance;
    private Double targetBalance;
    private LocalDate targetDate;
    private String icon;
    //private Double availableBalance;
    @Timestamp
    private LocalDateTime createdAt;
    @Timestamp
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
