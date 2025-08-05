package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class WalletMapperImpl implements WalletMapper{

    private final UserMapper userMapper;

    public WalletMapperImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public WalletResponse toWalletResponse(Wallet wallet,Double reservedBalance,Double availableBalance){
       WalletResponse walletResponse = new WalletResponse();
       walletResponse.setWalletId(wallet.getWalletId());
       walletResponse.setTotalBalance(wallet.getBalance());
       walletResponse.setReservedBalance(reservedBalance);
       walletResponse.setAvailableBalance(availableBalance);
       walletResponse.setUser(userMapper.toUserResponse(wallet.getUser()));
       walletResponse.setSubWallets(toListOfSubWalletResponse(wallet.getSubWallets()));
       return walletResponse;
   }

   private List<SubWalletResponse> toListOfSubWalletResponse(List<SubWallet> subWallets){
       List<SubWalletResponse> subWalletResponses = new ArrayList<>();
       for (SubWallet subWallet : subWallets){
           subWalletResponses.add(toSubWalletResponse(subWallet));
       }
       return subWalletResponses;
   }


    private SubWalletResponse toSubWalletResponse(SubWallet subWallet){
       SubWalletResponse subWalletResponse = new SubWalletResponse();
       subWalletResponse.setSubWalletName(subWallet.getSubWalletName());
       subWalletResponse.setAmount(subWallet.getBalance());
       return subWalletResponse;
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