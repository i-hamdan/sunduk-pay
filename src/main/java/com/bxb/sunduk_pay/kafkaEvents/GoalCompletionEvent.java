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

        /**
         * The ID of the user who achieved the milestone.
         */
        private String userId;

        /**
         * The email of the user who achieved the milestone.
         */
        private String email;

        /**
         * The ID of the wallet associated with the milestone.
         */
        private String walletId;

        /**
         * The name of the wallet associated with the milestone.
         */
        private String walletName;

        /**
         * The milestone percentage achieved (e.g., 50, 75, 100).
         */
        private int milestone;

        /**
         * The current balance of the wallet
         * at the time of milestone achievement.
         */
        private Double currentBalance;

        /**
         * The goal amount set for the wallet.
         */
        private Double goalAmount;

        /**
         * The timestamp when the milestone was achieved.
         */
        private LocalDateTime timestamp;
    }


