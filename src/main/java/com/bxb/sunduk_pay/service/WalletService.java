package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Service interface for wallet-related operations such as
 * balance retrieval, transaction handling, and wallet CRUD operations.
 */
public interface WalletService {

    /**
     * Returns the balance of a wallet by its ID.
     *
     * @param walletId the wallet ID
     * @return the balance as a formatted String
     */
    String showBalance(String walletId);

    /**
     * Downloads all transactions associated with a wallet.
     *
     * @param walletId the wallet ID
     * @param response the HttpServletResponse to write the download
     * @throws IOException if an I/O error occurs
     */
    void downloadTransactions(String walletId,
                              HttpServletResponse response) throws IOException;

    /**
     * Performs a payment operation from a wallet.
     *
     * @param mainWalletRequest the request containing payment details
     * @return the response of the payment operation
     */
    MainWalletResponse payMoney(MainWalletRequest mainWalletRequest);

    /**
     * Adds money to a wallet.
     *
     * @param mainWalletRequest the request containing add-money details
     * @return the response after adding money
     */
    MainWalletResponse addMoney(MainWalletRequest mainWalletRequest);

    /**
     * Handles wallet CRUD operations based on the request type.
     *
     * @param mainWalletRequest the request containing CRUD action
     * @return the response of the CRUD operation
     */
    MainWalletResponse walletCrud(MainWalletRequest mainWalletRequest);

    /**
     * Records a failed transaction.
     *
     * @param requestObj the request containing failed transaction details
     * @return the response confirming the failed transaction was recorded
     */
    MainWalletResponse recordFailedTxn(MainWalletRequest requestObj);

    /**
     * Adds dummy data for testing or initialization purposes.
     *
     * @param request the request containing dummy data details
     */
    void addDummy(MainWalletRequest request);
}
