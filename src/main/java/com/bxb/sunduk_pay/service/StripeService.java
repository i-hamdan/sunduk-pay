package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;

/**
 * Service interface for handling Stripe payment operations.
 */
public interface StripeService {

    /**
     * Creates a Stripe checkout session
     * for a given user and transaction details.
     *
     * @param userId        The ID of the user initiating the checkout session.
     * @param amount        The amount for the transaction.
     * @param transactionType The type of transaction (CREDIT or DEBIT).
     * @param targetWallet The wallet to which funds will be credited
     *                    (if applicable).
     * @param sourceWallet The wallet from which funds will be debited
     *                     (if applicable).
     * @throws Exception If an error occurs during session creation.
     * @return The created Stripe Session object.
     */
    Session createCheckoutSession(String userId, Double amount,
                                  TransactionType transactionType,
                                  WalletWrapper targetWallet,
                                  WalletWrapper sourceWallet) throws Exception;

}
