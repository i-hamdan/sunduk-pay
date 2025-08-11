package com.bxb.sunduk_pay.entityBuilder;

import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionBuilderImpl implements TransactionBuilder {

    @Override
    public Transaction createDebitTransaction(String subWalletId, MainWallet wallet, double amount, String description) {
        return Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description(description)
                .status("COMPLETED")
                .mainWallet(wallet)
                .subWalletId(subWalletId)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

    }

    @Override
    public Transaction createCreditTransaction(String subWalletId, MainWallet wallet, double amount, String description) {
        return Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description(description)
                .status("COMPLETED")
                .mainWallet(wallet)
                .subWalletId(subWalletId)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }
}
