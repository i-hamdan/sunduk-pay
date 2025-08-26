package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Validations {
    void validateBalance(Double balance, Double amount);

    User getUserInfo(String uuid);

    MainWallet getMainWalletByWalletId(String walletId);

    MainWallet getMainWalletInfo(String uuid);

    void validateNumberOfSubWallets(int size);

    Page<Transaction> validateTransactionsByUuidAndSubWalletId(String uuid, String walletId, String transactionGroupId, TransactionType transactionType, Pageable pageable);

    SubWallet findSubWalletIfExists(MainWallet wallet, String subWalletId);

    Boolean removeSubwallet(MainWallet wallet, String subWalletId);

    MasterWallet getMasterWalletInfo(String uuid);

}
