package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;

public interface Validations {
    void validateTransfer(SubWallet sourceWalletId, SubWallet targetWalletId, Double amount);
    User validateUserExists(String uuid);
    MainWallet validateWalletExistsForUser(String uuid);
    SubWallet validateSubWalletExists(MainWallet wallet, String subWalletId);

}
