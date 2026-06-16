package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;

/**
 * Service interface for handling wallet-related operations.
 */
public interface WalletService {

    /**
     * Processes a payment transaction.
     *
     * @param mainWalletRequest the request object containing payment details
     * @return the response object
     * containing the result of the payment transaction
     */
    MainWalletResponse payMoney(MainWalletRequest mainWalletRequest);

    /**
     * Adds money to the wallet.
     *
     * @param mainWalletRequest the request object containing details for
     *                         adding money
     * @return the response object containing the result of the add money
     * operation
     */
    MainWalletResponse addMoney(MainWalletRequest mainWalletRequest);

    /**
     * Performs CRUD operations on the wallet.
     *
     * @param mainWalletRequest the request object containing wallet details
     * @return the response object containing the result of the CRUD operation
     */
    MainWalletResponse walletCrud(MainWalletRequest mainWalletRequest);

    /**
     * Adds a dummy wallet for testing or demonstration purposes.
     *
     * @param request the request object containing details for the dummy wallet
     */
    void addDummy(MainWalletRequest request);
}
