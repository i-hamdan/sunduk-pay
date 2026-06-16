package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPotTransactionResponse {
    private String transactionId;
    private String globalPotId;
    private String globalWalletId;
    private Double amount;
    private String transactionType;
    private String userId;
    private String description;
    private String sourceUserTransactionId;
    private LocalDateTime createdAt;
}
