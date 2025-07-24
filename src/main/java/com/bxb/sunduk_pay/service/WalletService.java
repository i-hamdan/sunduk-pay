package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.PaginationRequest;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface WalletService {
    String createWallet(WalletRequest walletRequest);

    List<TransactionResponse> getRecentTransactionsByWalletId(String walletId, PaginationRequest request);

    WalletResponse getInfoByWalletId(String id);

    List<WalletsResponse> getAllWalletsByUuid(String uuid);

    String showBalance(String walletId);

    void downloadTransactions(String walletId, HttpServletResponse response) throws IOException;

    String deleteWallet(String walletId);
}
