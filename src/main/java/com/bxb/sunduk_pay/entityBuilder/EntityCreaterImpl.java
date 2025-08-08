package com.bxb.sunduk_pay.entityBuilder;

import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class EntityCreaterImpl implements EntityCreater {

    @Override
    public Transaction createInternalDebitTransaction(SubWallet sourceSubWallet, MainWallet wallet, double amount, SubWallet targetSubWallet) {
                return Transaction.builder()
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description("Transfer to SubWallet ID: " + targetSubWallet.getSubWalletId())
                .status("COMPLETED")
                .mainWalletId(sourceSubWallet)
                .masterWalletId(wallet)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

    }

    @Override
    public Transaction createInternalCreditTransaction(SubWallet sourceSubWallet, MainWallet wallet, double amount, SubWallet targetSubWallet) {
        return Transaction.builder()
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description("Transfer to SubWallet ID: " + targetSubWallet.getSubWalletId())
                .status("COMPLETED")
                .mainWalletId(sourceSubWallet)
                .masterWalletId(wallet)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }
}
