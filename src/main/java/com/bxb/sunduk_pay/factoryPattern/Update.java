package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;

import com.bxb.sunduk_pay.entityBuilder.EntityCreater;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class Update implements WalletOperation {
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final Validations validations;
    private final EntityCreater entityCreater;
    public Update(MainWalletRepository mainWalletRepository, TransactionRepository transactionRepository, UserRepository userRepository, WalletMapper walletMapper, Validations validations, EntityCreater entityCreater) {
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
        this.validations = validations;
        this.entityCreater = entityCreater;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    @Override
    public WalletResponse perform(WalletRequest walletRequest) {
        // is user Exists
        User user = validations.getUserInfo(walletRequest.getUuid());
        //  Validate wallet
        MainWallet wallet = validations.getMainWalletInfo(user.getUuid());
        //  3. Validate source and target subwallets
        SubWallet targetSubWallet = validations.validateSubWalletExists(wallet, walletRequest.getSubWalletId());

List<Transaction> transactions=new ArrayList<>();

        Double amount = walletRequest.getAmount();
        if (walletRequest.getActionType().equals(ActionType.ADD)) {
            //deduct from main wallet
            validations.checkMainWalletAmount(wallet, targetSubWallet, amount);
            wallet.setBalance(wallet.getBalance() - amount);
            wallet.setUpdatedAt(LocalDateTime.now());
            //add amount
            targetSubWallet.setBalance(targetSubWallet.getBalance() + amount);
            targetSubWallet.setUpdatedAt(LocalDateTime.now());
// debit for main Wallet
            transactions.add(entityCreater.createDebitTransaction(null, wallet, amount, targetSubWallet));
            // credit transaction for subWallet
            transactions.add(entityCreater.createCreditTransaction(targetSubWallet.getSubWalletId(), wallet, amount, targetSubWallet));
        }

        transactionRepository.saveAll(transactions);
        wallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(wallet);


        return WalletResponse.builder().message("Amount added Successfully").build();


    }


}
