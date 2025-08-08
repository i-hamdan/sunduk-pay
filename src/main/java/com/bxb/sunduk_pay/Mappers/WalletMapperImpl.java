package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class WalletMapperImpl implements WalletMapper{

   public WalletResponse toWalletResponse(MainWallet wallet){
       WalletResponse walletResponse = new WalletResponse();
       walletResponse.setWalletId(wallet.getMainWalletId());
       walletResponse.setBalance(wallet.getBalance());
       walletResponse.setUuid(wallet.getUser().getUuid());
       walletResponse.setSubWallets(wallet.getSubWallets());
       return walletResponse;
   }


//
//    public List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions){
//       List<TransactionResponse> responses = new ArrayList<>(transactions.size());
//       for(Transaction transaction : transactions){
//           responses.add(toTransactionResponse(transaction));
//       }
//   return responses;
//   }
//
//
//
//    private TransactionResponse toTransactionResponse(Transaction transaction){
//       TransactionResponse transactionResponse = new TransactionResponse();
//       transactionResponse.setUuid(transaction.getUser().getUuid());
//       transactionResponse.setTransactionType(transaction.getTransactionType());
//       transactionResponse.setAmount(transaction.getAmount());
//       transactionResponse.setDescription(transaction.getDescription());
//       transactionResponse.setDateTime(transaction.getDateTime());
//       transactionResponse.setMainWalletId(transaction.getMasterWalletId().getMainWalletId());
//       transactionResponse.setFullName(transaction.getMasterWalletId().getUser().getFullName());
//       return transactionResponse;
//    }

}