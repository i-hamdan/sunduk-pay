package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Validations {
    void checkMainWalletAmount(MainWallet mainWallet, SubWallet subWallet, Double amount);

    void checkSubWalletAmount (SubWallet subWallet, Double amount);

    User getUserInfo(String uuid);

    MainWallet getMainWalletInfo(String uuid);

    SubWallet validateSubWalletExists(MainWallet wallet, String subWalletId);

    boolean validateNumberOfSubWallets(int size);

    Page<Transaction> validateTransactionsBySubWalletId(String uuid, String subWalletId, Pageable pageable);

}
