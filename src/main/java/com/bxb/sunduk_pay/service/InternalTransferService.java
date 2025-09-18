package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

/**
 * Service interface for performing internal wallet transfers.
 */
public interface InternalTransferService {

    /**
     * Executes an internal transfer between wallets of a user.
     *
     * @param user                        the user performing the transfer
     * @param mainWallet                  the main wallet of the user
     * @param amount                      the amount to transfer
     * @param sourceWallet                the wallet from which funds are debited
     * @param targetWallet                the wallet to which funds are credited
     * @param previousSourceWalletBalance the previous balance of the source wallet
     * @param previousTargetWalletBalance the previous balance of the target wallet
     * @return a response containing updated wallet information
     */
    MainWalletResponse doInternalTransfer(
            User user,
            MainWallet mainWallet,
            Double amount,
            WalletWrapper sourceWallet,
            WalletWrapper targetWallet,
            Double previousSourceWalletBalance,
            Double previousTargetWalletBalance
    );
}
