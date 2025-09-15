package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Validations {
    void validateBalance(Double balance, Double amount);

    User getUserInfo(String uuid);

    MainWallet getMainWalletByWalletId(String walletId);

    MainWallet getMainWalletInfo(String uuid);

    void validateNumberOfSubWallets(int size);

    Page<Transaction> validateTransactionsByUuidAndSubWalletId(String uuid, String walletId, String transactionGroupId, PaymentMethod paymentMethod, TransactionType transactionType, Pageable pageable);

    SubWallet findSubWalletIfExists(MainWallet wallet, String subWalletId);

    SubWallet getSubWalletIfExists(MainWallet wallet, String subWalletId);

    String getFromIconOfTxn(String mainWalletId,String fromWalletId);

    String getToIconOfTxn(String mainWalletId,String toWalletId);

    Boolean removeSubwallet(MainWallet wallet, String subWalletId);

    MasterWallet getMasterWalletInfo(String uuid);

}
