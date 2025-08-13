package com.bxb.sunduk_pay.wrapper;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;

public class WalletWrapper {
    private final MainWallet mainWallet;
    private final SubWallet subWallet;
    private final  String walletId;

    public WalletWrapper(MainWallet mainWallet) {
        this.mainWallet = mainWallet;
        this.subWallet = null;
        this.walletId = null;
    }

    public WalletWrapper(SubWallet subWallet) {
        this.subWallet = subWallet;
        this.mainWallet = null;
        this.walletId = null;
    }

    public WalletWrapper(String walletId){
        this.walletId = walletId;
        this.subWallet = null;
        this.mainWallet = null;
    }
    public String getId() {
        return mainWallet != null ? mainWallet.getMainWalletId() : subWallet.getSubWalletId();
    }

    public String getName() {
        return mainWallet != null ? "Main Wallet" : subWallet.getSubWalletName();
    }

    public double getBalance() {
        return mainWallet != null ? mainWallet.getBalance() : subWallet.getBalance();
    }

    public void setBalance(double newBalance) {
        if (mainWallet != null) {
            mainWallet.setBalance(newBalance);
        } else {
            subWallet.setBalance(newBalance);
        }
    }
}