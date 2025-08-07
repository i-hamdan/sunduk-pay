package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface WalletService {
    String createWallet(WalletRequest walletRequest);

    String showBalance(String walletId);

    void downloadTransactions(String walletId, HttpServletResponse response) throws IOException;

    String addMoneyToWallet(String userId, double amount, String paymentIntentId);

    String payMoneyFromWallet(String userId, double amount, String description);

   List<TransactionResponse> getAllTransactions(String uuid, String walletId);


    String check(WalletRequest request);
}
