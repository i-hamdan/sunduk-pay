package com.bxb.sunduk_pay.entityBuilder;

import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;

public interface TransactionBuilder {
    Transaction createDebitTransaction(String subWalletId, MainWallet wallet, double amount, String description);
    Transaction createCreditTransaction(String subWalletId, MainWallet wallet, double amount,String description);

}
