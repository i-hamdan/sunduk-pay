package com.bxb.sunduk_pay.entityBuilder;

import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;

public interface EntityCreater {
    Transaction createDebitTransaction(String subWalletId, MainWallet wallet, double amount, SubWallet targetSubWallet);
    Transaction createCreditTransaction(String subWalletId, MainWallet wallet, double amount, SubWallet targetSubWallet);

}
