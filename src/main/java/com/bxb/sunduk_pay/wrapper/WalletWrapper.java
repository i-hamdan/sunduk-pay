package com.bxb.sunduk_pay.wrapper;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;

/**
 * This class can hold either a MainWallet or a SubWallet.
 * Provides a unified interface
 * to access common properties of both wallet types.
 */
public class WalletWrapper {
    /** The MainWallet instance, if applicable. */
    private final MainWallet mainWallet;
    /** The SubWallet instance, if applicable. */
    private final SubWallet subWallet;

    /** Constructor to create a WalletWrapper for a MainWallet.
     * @param mainWalletParam the MainWallet instance to wrap
     */
    public WalletWrapper(final MainWallet mainWalletParam) {
        this.mainWallet = mainWalletParam;
        this.subWallet = null;
    }

    /** Constructor to create a WalletWrapper for a SubWallet.
     * @param subWalletParam the SubWallet instance to wrap
     */
    public WalletWrapper(final SubWallet subWalletParam) {
        this.subWallet = subWalletParam;
        this.mainWallet = null;
    }


    /** Gets the ID of the underlying wallet.
     * @return the ID of the MainWallet or SubWallet
     */
    public Long getId() {
        return mainWallet != null
                ? mainWallet.getMainWalletId()
                : subWallet.getSubWalletId();
    }

    /** Gets the name of the underlying wallet.
     * @return "Main Wallet" if it's a MainWallet,
     * otherwise the name of the SubWallet
     */
    public String getName() {
        return mainWallet != null
                ? "Main Wallet" : subWallet.getSubWalletName();
    }


    /** Gets the balance of the underlying wallet.
     * @return the balance of the MainWallet or SubWallet
     */
    public double getBalance() {
        return mainWallet != null
                ? mainWallet.getBalance() : subWallet.getBalance();
    }


    /** Gets the goal amount of the underlying wallet.
     * @return the target balance if it's a SubWallet,
     * otherwise null
     */
    public Double getGoalAmount() {
        return subWallet != null
                ? subWallet.getTargetBalance() : null;
    }
    /** Sets the balance of the underlying wallet.
     * @param newBalance the new balance to set
     */
    public void setBalance(final double newBalance) {
        if (mainWallet != null) {
            mainWallet.setBalance(newBalance);
        } else {
            subWallet.setBalance(newBalance);
        }
    }
}
