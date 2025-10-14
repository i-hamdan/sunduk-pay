package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
import java.util.List;

/**
 * Response object for main wallet operations,
 * including details about the main wallet,
 * associated sub-wallets,
 * transaction history, and transfer details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainWalletResponse {
    /**
     * Status of the operation (e.g., success, failure).
     */
    private String status;
    /** User identifier associated with the transaction. */
    private Long uuid;
    /**
     * Identifier of the main wallet.
     */
    private Long mainWalletId; // main walletId

    /**
     * amount of balance in the main wallet.
     */
    private Double balance;
    /**
     * List of sub-wallets
     * associated with the main wallet.
     */
    private List<SubWalletResponse> subWallets;

    /**
     * Target balance for the main wallet.
     */

    private Double targetBalance;
    /**
     * List of transactions
     * associated with the main wallet.
     */

    private List<TransactionResponse> transactionHistory;
    /**
     * Identifier of the source sub-wallet
     * from which funds are being transferred.
     */
    private Long sourceSubWalletId;
    /**
     * Previous balance in the source wallet
     * before the transfer operation is executed.
     */
    private Double previousSourceWalletBalance;
    /**
     * New balance in the source wallet
     * after the transfer operation is completed.
     */
    private Double newSourceWalletBalance;
    /**
     * Available balance in the source wallet
     * after the transfer operation is completed.
     */
    private Double sourceAvailableBalance;
    /**
     * Identifier of the target sub-wallet
     * to which funds are being transferred.
     */
    private Long targetSubWalletId;
    /**
     * Previous balance in the target wallet
     * before the transfer operation is executed.
     */
    private Double previousTargetWalletBalance;
    /**
     * New balance in the target wallet
     * after the transfer operation is completed.
     */
    private Double newTargetWalletBalance;
    /**
     * Available balance in the target wallet
     * after the transfer operation is completed.
     */
    private Double targetAvailableBalance;
    /**
     * Amount transferred from the source wallet
     * to the target wallet as part of the transfer operation.
     */
    private Double transferredAmount;
    /**
     * Identifier of the transaction
     * created in the source wallet
     * as part of the transfer operation.
     */
    private Long sourceTransactionId;
    /**
     * Identifier of the transaction
     * created in the target wallet
     * as part of the transfer operation.
     */
    private Long targetTransactionId;

    /**
     * Message providing additional
     * information about the operation.
     */
    private String message;
    /** Checkout session id for payment processing. */
    private String session;
    /** URL to redirect the user for completing payment. */
    private String checkoutUrl;
}

