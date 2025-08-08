package com.bxb.sunduk_pay.entityBuilder;

import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;

public interface EntityCreater {
    Transaction createInternalDebitTransaction(String subWalletId, MainWallet wallet, double amount, SubWallet targetSubWallet);
    Transaction createInternalCreditTransaction(String subWalletId, MainWallet wallet, double amount, SubWallet targetSubWallet);
}
