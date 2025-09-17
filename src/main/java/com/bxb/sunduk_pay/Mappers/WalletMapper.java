package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;

import java.util.List;

public interface WalletMapper {
     WalletResponse toWalletResponse(Wallet wallet);
     List<WalletsResponse> toWalletsResponse(List<Wallet> wallets);
    List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions);
}
