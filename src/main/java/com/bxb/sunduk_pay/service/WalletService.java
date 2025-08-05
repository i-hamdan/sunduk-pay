package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.SubWalletRequest;
import com.bxb.sunduk_pay.request.SubWalletTransferRequest;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface WalletService {
    String createWallet(WalletRequest walletRequest);


    String showBalance(String walletId);

    void downloadTransactions(String walletId, HttpServletResponse response) throws IOException;

    String addMoneyToWallet(String userId, double amount, String paymentIntentId);

    String payMoneyFromWallet(String userId, double amount, String description);

   List<TransactionResponse> getAllTransactions(String uuid,String type);

    void createSubWallet(SubWalletRequest request);

    String addMoneyToSubWallet(SubWalletTransferRequest request);

    WalletResponse getInfoByUuid(String uuid);
}
