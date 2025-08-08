package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import org.springframework.stereotype.Component;


@Component
public class WalletMapperImpl implements WalletMapper{

   public MainWalletResponse toWalletResponse(MainWallet wallet){
       MainWalletResponse mainWalletResponse = new MainWalletResponse();
       mainWalletResponse.setWalletId(wallet.getMainWalletId());
       mainWalletResponse.setBalance(wallet.getBalance());
       mainWalletResponse.setUuid(wallet.getUser().getUuid());
       mainWalletResponse.setSubWallets(wallet.getSubWallets());
       return mainWalletResponse;
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