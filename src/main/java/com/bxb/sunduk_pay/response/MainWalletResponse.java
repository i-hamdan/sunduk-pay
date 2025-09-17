package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * Response payload for main wallet operations.
 * <p>
 * Includes wallet details, balances, sub-wallet information, transaction history,
 * transfer details, and optional Stripe payment session info.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainWalletResponse {

    /** Status of the operation (e.g., SUCCESS, FAILURE). */
    private String status;

    /** User UUID. */
    private String uuid;

    /** Main wallet ID. */
    private String mainWalletId;

    /** Current balance of the main wallet. */
    private Double balance;

    /** List of sub-wallet responses associated with the main wallet. */
    private List<SubWalletResponse> subWallets;

    /** Target balance of the main wallet, if applicable. */
    private Double targetBalance;

    /** Transaction history for the main wallet. */
    private List<TransactionResponse> transactionHistory;

    /** Source sub-wallet ID for transfers. */
    private String sourceSubWalletId;

    /** Previous balance of the source wallet before transfer. */
    private Double previousSourceWalletBalance;

    /** New balance of the source wallet after transfer. */
    private Double newSourceWalletBalance;

    /** Available balance of the source wallet. */
    private Double sourceAvailableBalance;

    /** Target sub-wallet ID for transfers. */
    private String targetSubWalletId;

    /** Previous balance of the target wallet before transfer. */
    private Double previousTargetWalletBalance;

    /** New balance of the target wallet after transfer. */
    private Double newTargetWalletBalance;

    /** Available balance of the target wallet. */
    private Double targetAvailableBalance;

    /** Amount transferred between wallets. */
    private Double transferredAmount;

    /** Transaction ID for the source wallet. */
    private String sourceTransactionId;

    /** Transaction ID for the target wallet. */
    private String targetTransactionId;

    /** Transaction group ID for grouped transfers. */
    private String transactionGroupId;

    /** Optional message related to the operation. */
    private String message;

    /** Stripe session ID (for Stripe integration). */
    private String session;

    /** Stripe checkout URL (for Stripe integration). */
    private String checkoutUrl;
}
