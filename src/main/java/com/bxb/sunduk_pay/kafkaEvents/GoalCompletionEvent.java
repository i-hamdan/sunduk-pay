package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kafka event published when a user reaches a goal milestone in a wallet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletionEvent {

    /**
     * ID of the user who achieved the milestone.
     */
    private String userId;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * ID of the wallet where the milestone was reached.
     */
    private String walletId;

    /**
     * Name of the wallet.
     */
    private String walletName;

    /**
     * Goal milestone reached (e.g., 50, 75, 100).
     */
    private int milestone;

    /**
     * Current balance at the time of the milestone.
     */
    private Double currentBalance;

    /**
     * Target goal amount of the wallet.
     */
    private Double goalAmount;

    /**
     * Timestamp of when the milestone was reached.
     */
    private LocalDateTime timestamp;
}
