package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.entityBuilder.TransactionBuilder;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TransferService implements WalletOperation {
    private final Validations validations;
    private final TransactionBuilder transactionBuilder;
    private final TransactionRepository transactionRepository;
    private final MainWalletRepository mainWalletRepository;
    private final PaymentService paymentService;

    public TransferService(Validations validations, TransactionBuilder transactionBuilder, TransactionRepository transactionRepository, MainWalletRepository mainWalletRepository, PaymentService paymentService) {
        this.validations = validations;
        this.transactionBuilder = transactionBuilder;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.paymentService = paymentService;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_MONEY;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        User user = validations.getUserInfo(mainWalletRequest.getUuid());

        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());

        validations.findSubWalletIfExists(mainWallet,mainWalletRequest.getSourceWalletId());
        validations.findSubWalletIfExists(mainWallet,mainWalletRequest.getTargetWalletId());
        WalletWrapper sourceWallet = getWallet(mainWallet, mainWalletRequest.getSourceWalletId());



        Double previousSourceWalletBalance;
        if(sourceWallet==null){
            previousSourceWalletBalance= null;
        }else {
            previousSourceWalletBalance= sourceWallet.getBalance();
        }


        WalletWrapper targetWallet = getWallet(mainWallet, mainWalletRequest.getTargetWalletId());
        Double previousTargetWalletBalance;
        if(targetWallet==null){
            previousTargetWalletBalance= null;
        }else {
            previousTargetWalletBalance= targetWallet.getBalance();

        }


        boolean sourceExists = (sourceWallet!=null);
        boolean targetExists = (targetWallet!=null);

        if (sourceExists && targetExists) {
            return handleInternalTransfer(user,mainWallet,mainWalletRequest.getAmount(),sourceWallet,targetWallet,previousSourceWalletBalance,previousTargetWalletBalance);
        } else if (sourceExists && !targetExists) {
            return handleExternalOutGoingTransfer(sourceWallet, mainWalletRequest.getAmount(), previousSourceWalletBalance, mainWalletRequest, mainWallet,targetWallet);
        } else if (!sourceExists && targetExists) {
            return handleExternalIncomingTransfer(mainWallet, targetWallet,mainWalletRequest.getAmount(),mainWalletRequest,sourceWallet);
        } else {
            throw new ResourceNotFoundException("both source and target is invalid for this user");
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(MainWallet mainWallet, WalletWrapper targetWallet, Double amount, MainWalletRequest mainWalletRequest, WalletWrapper sourceWallet) {

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        return paymentService.createCheckoutSession(user.getUuid(), amount,mainWalletRequest.getTransactionType(),targetWallet,sourceWallet);

    }

    private MainWalletResponse handleExternalOutGoingTransfer(WalletWrapper sourceWallet, Double amount, Double previousSourceWalletBalance, MainWalletRequest mainWalletRequest, MainWallet mainWallet, WalletWrapper targetWallet) {

        if (amount > previousSourceWalletBalance) {
            throw new InsufficientBalanceException("You do not have enough funds");
        }

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        return paymentService.createCheckoutSession(user.getUuid(), amount, mainWalletRequest.getTransactionType(),sourceWallet,targetWallet);

    }





    public MainWalletResponse handleInternalTransfer(User user,MainWallet mainWallet,Double amount ,WalletWrapper sourceWallet, WalletWrapper targetWallet, Double previousSourceWalletBalance, Double previousTargetWalletBalance) {
        List<Transaction> transactions = new ArrayList<>();

        validations.validateBalance(sourceWallet.getBalance(),amount);
        sourceWallet.setBalance(sourceWallet.getBalance() -amount);
        Double newSourceWalletBalance = sourceWallet.getBalance();
        Transaction debitTransaction = transactionBuilder.createDebitTransaction(sourceWallet.getId(), mainWallet,amount, "Transfer to " + targetWallet.getName());
        transactions.add(debitTransaction);


        targetWallet.setBalance(targetWallet.getBalance() + amount);
        Double newTargetWalletBalance = targetWallet.getBalance();
        Transaction creditTransaction = transactionBuilder.createCreditTransaction(targetWallet.getId(), mainWallet, amount, "Received from " + sourceWallet.getName());
        transactions.add(creditTransaction);

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        return MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(transactions.get(0).getTransactionId())
                .targetTransactionId(transactions.get(1).getTransactionId())
                .previousSourceWalletBalance(previousSourceWalletBalance)
                .newSourceWalletBalance(newSourceWalletBalance)
                .previousTargetWalletBalance(previousTargetWalletBalance)
                .newTargetWalletBalance(newTargetWalletBalance)
                .message("Transfer successful")
                .build();
    }
    private WalletWrapper getWallet(MainWallet mainWallet, String walletId) {
        if (walletId == null) {
            throw new ResourceNotFoundException("Wallet ID cannot be null");
        }
        if (walletId.equals(mainWallet.getMainWalletId())) {
            return new WalletWrapper(mainWallet);
        }
        SubWallet subWallet = validations.validateSubWalletExists(mainWallet, walletId);
        return (subWallet != null) ? new WalletWrapper(subWallet) : null;
    }
}


