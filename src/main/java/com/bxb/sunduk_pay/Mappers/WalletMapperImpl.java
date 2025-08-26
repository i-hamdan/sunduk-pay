package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class WalletMapperImpl implements WalletMapper{

   public MainWalletResponse toWalletResponse(MainWallet wallet,List<SubWallet> subWallets){
       MainWalletResponse mainWalletResponse = new MainWalletResponse();
       mainWalletResponse.setMainWalletId(wallet.getMainWalletId());
       mainWalletResponse.setBalance(wallet.getBalance());
       mainWalletResponse.setUuid(wallet.getUser().getUuid());
       mainWalletResponse.setSubWallets(toSubWalletResponseList(subWallets));
       return mainWalletResponse;
   }

   private List<SubWalletResponse> toSubWalletResponseList(List<SubWallet> subWallet){
       List<SubWalletResponse> list = new ArrayList<>();
       for (SubWallet subWallet1 : subWallet){
           list.add(toSubWalletResponse(subWallet1));
       }
       return list;
   }

   private SubWalletResponse toSubWalletResponse(SubWallet subWallet){
       SubWalletResponse subWalletResponse=new SubWalletResponse();
       subWalletResponse.setSubWalletId(subWallet.getSubWalletId());
       subWalletResponse.setSubWalletName(subWallet.getSubWalletName());
       subWalletResponse.setBalance(subWallet.getBalance());
       subWalletResponse.setTargetBalance(subWallet.getTargetBalance());
       return subWalletResponse;
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