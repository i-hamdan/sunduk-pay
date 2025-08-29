package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface WalletService {
//    String createWallet(WalletRequest walletRequest);

    String showBalance(String walletId);

    void downloadTransactions(String walletId, HttpServletResponse response) throws IOException;

//    String addMoneyToWallet(String userId, double amount, String paymentIntentId);

    MainWalletResponse payMoney(MainWalletRequest mainWalletRequest);

    MainWalletResponse addMoney(MainWalletRequest mainWalletRequest);

    MainWalletResponse walletCrud(MainWalletRequest mainWalletRequest);
}
