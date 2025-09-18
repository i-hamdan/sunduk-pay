package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

/**
 * Service interface for handling payment-related operations.
 */
public interface PaymentService {

    /**
     * Creates a checkout session for a payment.
     *
     * @param userId          the ID of the user initiating the payment
     * @param amount          the amount to be transacted
     * @param transactionType the type of transaction (CREDIT or DEBIT)
     * @param targetWallet    the wallet receiving the funds
     * @param sourceWallet    the wallet sending the funds
     * @return a response containing the status and details of the payment session
     */
    MainWalletResponse createCheckoutSession(
            String userId,
            Double amount,
            TransactionType transactionType,
            WalletWrapper targetWallet,
            WalletWrapper sourceWallet
    );
}
