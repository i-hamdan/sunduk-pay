package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Service interface for handling wallet-related operations.
 */
public interface WalletService {

    /**
     * Displays the balance of the specified wallet.
     *
     * @param walletId the ID of the wallet
     * @return the balance as a String
     */
    String showBalance(String walletId);

//    /**
//     * Downloads the transaction history of the specified wallet and
//     * writes it to the HTTP response.
//     *
//     * @param walletId the ID of the wallet
//     * @param response the
//     *HttpServletResponse to write the transaction history to
//     * @throws IOException if an I/O error occurs
//     */
//    void downloadTransactions(Long walletId,
//                              HttpServletResponse response) throws IOException;

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
