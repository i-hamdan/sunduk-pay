package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.request.PaginationRequest;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface WalletService {
    String createWallet(WalletRequest walletRequest);

    String showBalance(String walletId);

    void downloadTransactions(String walletId, HttpServletResponse response) throws IOException;

    void addMoneyToWallet(String userId, double amount, String paymentIntentId);

    void payMoneyFromWallet(String userId, double amount, String description);

   List<TransactionResponse> getAllTransactions(String uuid, String walletId);
}
