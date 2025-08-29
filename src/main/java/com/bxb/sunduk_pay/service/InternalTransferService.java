package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

public interface InternalTransferService {
    MainWalletResponse doInternalTransfer(User user, MainWallet mainWallet, Double amount,
                                                 WalletWrapper sourceWallet, WalletWrapper targetWallet,
                                                 Double previousSourceWalletBalance, Double previousTargetWalletBalance);
}
