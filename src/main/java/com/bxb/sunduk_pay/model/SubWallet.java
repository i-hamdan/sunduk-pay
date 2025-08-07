package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.SubWalletType;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
public class SubWallet {
    @Id
    private String subWalletId;
    private String subWalletName;
    private Double balance;
    private SubWalletType subWalletType;
    @Timestamp
    private LocalDateTime createdAt;
    @Timestamp
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private Double targetBalance;
    private Double availableBalance;
}
