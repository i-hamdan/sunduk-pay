package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

/**
 * Service interface for handling internal transfers between wallets.
 */
public interface InternalTransferService {

    /**
     * Performs an internal transfer between two wallets.
     *
     * @param user The user initiating the transfer.
     * @param mainWallet The main wallet associated with the user.
     * @param amount The amount to be transferred.
     * @param sourceWallet The wallet from which the amount will be deducted.
     * @param targetWallet The wallet to which the amount will be credited.
     * @param previousSourceWalletBalance The balance of the source wallet
     *                                    before the transfer.
     * @param previousTargetWalletBalance The balance of the target
     *                                    wallet before the transfer.
     * @return A response object containing details of the transfer operation.
     */
    MainWalletResponse doInternalTransfer(User user, MainWallet mainWallet,
                                          Double amount,
                                          WalletWrapper sourceWallet,
                                          WalletWrapper targetWallet,
                                          Double previousSourceWalletBalance,
                                          Double previousTargetWalletBalance);
}

