package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.InvalidUserException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class Update implements WalletOperation {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    public Update(WalletRepository walletRepository, TransactionRepository transactionRepository, UserRepository userRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    @Override
    public WalletResponse perform(WalletRequest walletRequest) {
        User user = userRepository.findById(walletRequest.getUuid())
                .orElseThrow(() -> new InvalidUserException("User id not found"));

        Wallet wallet = walletRepository.findByUser_Uuid(user.getUuid()).orElseThrow(() -> new WalletNotFoundException("wallet not found for this user"));

        SubWallet sourceSubWallet = wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(walletRequest.getSubWalletId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Source SubWallet not found"));

        SubWallet targetSubWallet = wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(walletRequest.getTargetSubwalletId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Target SubWallet not found"));


        Double amount = walletRequest.getAmount();

if (sourceSubWallet.getAvailableBalance()<amount){
    throw new InsufficientBalanceException("Insufficient Balance in source sub wallet");
}
// deduct amount from one wallet (jisse pay kr rhe hai)
sourceSubWallet.setAvailableBalance(sourceSubWallet.getAvailableBalance() - amount);
sourceSubWallet.setUpdatedAt(LocalDateTime.now());
//add amount
targetSubWallet.setAvailableBalance(targetSubWallet.getAvailableBalance() + amount);
targetSubWallet.setUpdatedAt(LocalDateTime.now());

        Transaction debitTxn = Transaction.builder()
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description("Transfer to SubWallet ID: " + targetSubWallet.getSubWalletId())
                .status("COMPLETED")
                .subWallet(sourceSubWallet)
                .wallet(wallet)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Transaction creditTxn = Transaction.builder()
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.INTERNAL)
                .amount(amount)
                .description("Received from SubWallet ID: " + sourceSubWallet.getSubWalletId())
                .status("COMPLETED")
                .subWallet(targetSubWallet)
                .wallet(wallet)
                .user(wallet.getUser())
                .dateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        List<Transaction>transactions =new ArrayList<>();
        transactions.add(creditTxn);
        transactions.add(debitTxn);
        transactionRepository.saveAll(transactions);
        wallet.getTransactionHistory().addAll(transactions);
        walletRepository.save(wallet);


        WalletResponse walletResponse = walletMapper.toWalletResponse(wallet);
        return walletResponse;


    }


}
