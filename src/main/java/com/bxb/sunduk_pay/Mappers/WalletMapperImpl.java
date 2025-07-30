package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class WalletMapperImpl implements WalletMapper{

   public WalletResponse toWalletResponse(Wallet wallet){
       WalletResponse walletResponse = new WalletResponse();
       walletResponse.setWalletId(wallet.getWalletId());
       walletResponse.setBalance(wallet.getBalance());
       walletResponse.setUser(wallet.getUser());
       return walletResponse;
   }

    public List<WalletsResponse> toWalletsResponse(List<Wallet> wallets){
       List<WalletsResponse> walletList = new ArrayList<>(wallets.size());
    for (Wallet wallet:wallets){
        walletList.add(walletToWalletsResponse(wallet));
    }
    return walletList;
    }


        private WalletsResponse walletToWalletsResponse(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletsResponse walletsResponse = new WalletsResponse();

        walletsResponse.setWalletId( wallet.getWalletId() );
        walletsResponse.setBalance( wallet.getBalance() );
        walletsResponse.setUser( wallet.getUser() );

        return walletsResponse;
    }



    public List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions){
       List<TransactionResponse> responses = new ArrayList<>(transactions.size());
       for(Transaction transaction : transactions){
           responses.add(toTransactionResponse(transaction));
       }
   return responses;
   }



    private TransactionResponse toTransactionResponse(Transaction transaction){
       TransactionResponse transactionResponse = new TransactionResponse();
       transactionResponse.setUuid(transaction.getUser().getUuid());
       transactionResponse.setTransactionType(transaction.getTransactionType());
       transactionResponse.setAmount(transaction.getAmount());
       transactionResponse.setDescription(transaction.getDescription());
       transactionResponse.setDateTime(transaction.getDateTime());
       transactionResponse.setWalletId(transaction.getWallet().getWalletId());
       transactionResponse.setFullName(transaction.getWallet().getUser().getFullName());
       return transactionResponse;
    }

}