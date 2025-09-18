package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;

/**
 * Service interface for handling Stripe payment operations.
 */
public interface StripeService {

    /**
     * Creates a Stripe checkout session for a given transaction.
     *
     * @param userId the UUID of the user initiating the transaction
     * @param amount   the transaction amount
     * @param transactionType the type of transaction (CREDIT or DEBIT)
     * @param targetWallet the target wallet for the transaction
     * @param sourceWallet the source wallet for the transaction
     * @return the created Stripe Checkout Session
     * @throws Exception if the session creation fails
     */
    Session createCheckoutSession(
            String userId,
            Double amount,
            TransactionType transactionType,
            WalletWrapper targetWallet,
            WalletWrapper sourceWallet
    ) throws Exception;
}
