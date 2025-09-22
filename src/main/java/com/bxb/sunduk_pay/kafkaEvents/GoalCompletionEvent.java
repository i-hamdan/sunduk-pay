package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing the completion of a savings goal milestone.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
    @Builder
    public class GoalCompletionEvent {
        private String userId;
        private String email;
        private String walletId;
        private String walletName;
        private int milestone; // 50, 75, 100
        private Double currentBalance;
        private Double goalAmount;
        private LocalDateTime timestamp;
    }


