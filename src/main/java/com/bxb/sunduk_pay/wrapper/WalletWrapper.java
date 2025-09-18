package com.bxb.sunduk_pay.wrapper;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;

/**
 * Wrapper class to handle both MainWallet and SubWallet uniformly.
 */
public class WalletWrapper {

    private final MainWallet mainWallet;
    private final SubWallet subWallet;

    /**
     * Constructor for wrapping a MainWallet.
     *
     * @param mainWallet the main wallet to wrap
     */
    public WalletWrapper(MainWallet mainWallet) {
        this.mainWallet = mainWallet;
        this.subWallet = null;
    }

    /**
     * Constructor for wrapping a SubWallet.
     *
     * @param subWallet the sub-wallet to wrap
     */
    public WalletWrapper(SubWallet subWallet) {
        this.subWallet = subWallet;
        this.mainWallet = null;
    }

    /**
     * Returns the ID of the wallet.
     *
     * @return main wallet ID if present; otherwise, sub-wallet ID
     */
    public String getId() {
        return mainWallet != null ? mainWallet.getMainWalletId() : subWallet.getSubWalletId();
    }

    /**
     * Returns the display name of the wallet.
     *
     * @return "Main Wallet" for main wallet; otherwise, sub-wallet name
     */
    public String getName() {
        return mainWallet != null ? "Main Wallet" : subWallet.getSubWalletName();
    }

    /**
     * Returns the balance of the wallet.
     *
     * @return balance amount
     */
    public double getBalance() {
        return mainWallet != null ? mainWallet.getBalance() : subWallet.getBalance();
    }

    /**
     * Returns the goal amount for a sub-wallet.
     *
     * @return target balance if sub-wallet; otherwise, null
     */
    public Double getGoalAmount() {
        return subWallet != null ? subWallet.getTargetBalance() : null;
    }

    /**
     * Updates the balance of the wallet.
     *
     * @param newBalance the new balance to set
     */
    public void setBalance(double newBalance) {
        if (mainWallet != null) {
            mainWallet.setBalance(newBalance);
        } else {
            subWallet.setBalance(newBalance);
        }
    }
}
