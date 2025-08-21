package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;

import com.bxb.sunduk_pay.entityBuilder.TransactionBuilder;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UpdateService implements WalletOperation {
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final Validations validations;
    private final TransactionBuilder transactionBuilder;
    public UpdateService(MainWalletRepository mainWalletRepository, TransactionRepository transactionRepository, UserRepository userRepository, WalletMapper walletMapper, Validations validations, TransactionBuilder transactionBuilder) {
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
        this.validations = validations;
        this.transactionBuilder = transactionBuilder;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        // is user Exists
        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        //  Validate mainWallet
        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
        //  3. Validate source and target subwallets
        SubWallet targetSubWallet = validations.validateSubWalletExists(mainWallet, mainWalletRequest.getSubWalletId());

List<Transaction> transactions = new ArrayList<>();

        Double amount = mainWalletRequest.getAmount();
        if (mainWalletRequest.getActionType().equals(ActionType.ADD)) {
            //deduct from main mainWallet
            validations.checkMainWalletAmount(mainWallet, targetSubWallet, amount);
            mainWallet.setBalance(mainWallet.getBalance() - amount);
            mainWallet.setUpdatedAt(LocalDateTime.now());
            //add amount
            targetSubWallet.setBalance(targetSubWallet.getBalance() + amount);
            targetSubWallet.setUpdatedAt(LocalDateTime.now());
// debit for main Wallet
            transactions.add(transactionBuilder.createDebitTransaction(null, mainWallet, amount, "Transfer to subWallet : "+targetSubWallet.getSubWalletName()));
            // credit transaction for subWallet
            transactions.add(transactionBuilder.createCreditTransaction(targetSubWallet.getSubWalletId(), mainWallet, amount, "Received from main wallet"));
        }

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);


        return MainWalletResponse.builder().message("Amount added Successfully").build();


    }


}
