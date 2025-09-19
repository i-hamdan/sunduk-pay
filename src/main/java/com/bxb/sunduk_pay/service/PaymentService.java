package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

/**
 * Service interface for handling payment-related operations.
 */
public interface PaymentService {

    /**
     * Creates a checkout session for a payment transaction.
     *
     * @param userId The ID of the user initiating the transaction.
     * @param amount The amount to be processed in the transaction.
     * @param transactionType The type of transaction (e.g., CREDIT, DEBIT).
     * @param targetWallet The wallet to which the amount will be credited.
     * @param sourceWallet The wallet from which the amount will be debited.
     * @return A response object containing details of the checkout session.
     */
    MainWalletResponse createCheckoutSession(String userId, Double amount,
                                             TransactionType transactionType,
                                             WalletWrapper targetWallet,
                                             WalletWrapper sourceWallet);
}
