package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.entityBuilder.TransactionBuilder;
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
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        SubWallet sourceSubWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getSourceSubWalletId());
        Double sourceSubWalletBalance = sourceSubWallet.getBalance();

        SubWallet targetSubWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getTargetSubWalletId());
        Double targetSubWalletBalance = targetSubWallet.getBalance();


        boolean sourceExists = (mainWalletRequest.getSourceSubWalletId() != null);
        boolean targetExists = (mainWalletRequest.getTargetSubWalletId() != null);

        if (sourceExists && targetExists) {
            return handleInternalTransfer(sourceSubWallet, targetSubWallet, mainWalletRequest.getAmount(), mainWallet, targetSubWalletBalance, sourceSubWalletBalance);
        } else if (sourceExists && !targetExists) {
            return handleExternalOutGoingTransfer(sourceSubWallet, mainWalletRequest.getAmount(), sourceSubWalletBalance, mainWalletRequest, mainWallet );
        } else if (!sourceExists && targetExists) {
            return handleExternalIncomingTransfer(mainWallet, targetSubWallet);
        } else {
            throw new ResourceNotFoundException("both source and target is invalid for this user");
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(MainWallet mainWallet, SubWallet targetSubWallet) {
        return null;
    }


    private MainWalletResponse handleExternalOutGoingTransfer(SubWallet sourceSubWallet, Double amount, Double sourceSubWalletBalance, MainWalletRequest mainWalletRequest, MainWallet mainWallet) {


        if (amount > sourceSubWalletBalance) {
            throw new RuntimeException("low balance");
        }

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        paymentService.createCheckoutSession(user.getUuid(), amount, mainWalletRequest.getTransactionType());

        mainWallet.setBalance(mainWallet.getBalance() - amount);
        sourceSubWallet.setBalance(sourceSubWallet.getBalance() - amount);

        Transaction transaction= Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(amount)
                .transactionType(mainWalletRequest.getTransactionType())
                .transactionLevel(TransactionLevel.EXTERNAL)
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .build();
        transactionRepository.save(transaction);
        mainWallet.getTransactionHistory().add(transaction);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(transaction.getTransactionId())
                .message("Transfer Successful")
                .build();

        return response;
    }


    private MainWalletResponse handleInternalTransfer(SubWallet sourceSubWallet, SubWallet targetSubWallet, Double amount, MainWallet mainWallet, double targetSubWalletBalance, double sourceSubWalletBalance) {
        List<Transaction> transactions = new ArrayList<>();
        //deducting balance from sourceWallet.
        validations.checkSubWalletAmount(sourceSubWallet, amount);
        sourceSubWallet.setBalance(sourceSubWallet.getBalance() - amount);
        Double newSourceSubWalletBalance = sourceSubWallet.getBalance();
        Transaction debitTransaction = transactionBuilder.createDebitTransaction(sourceSubWallet.getSubWalletId(), mainWallet, amount, "Transfer to subWallet : " + targetSubWallet.getSubWalletName());
        transactions.add(debitTransaction);
        //adding balance to target subWallet.
        targetSubWallet.setBalance(targetSubWallet.getBalance() + amount);
        Double newTargetSubWalletBalance = targetSubWallet.getBalance();
        Transaction creditTransaction = transactionBuilder.createCreditTransaction(targetSubWallet.getSubWalletId(), mainWallet, amount, "Received from subWallet : " + sourceSubWallet.getSubWalletName());
        transactions.add(creditTransaction);

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        return MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(debitTransaction.getTransactionId())
                .targetTransactionId(creditTransaction.getTransactionId())
                .previousSourceWalletBalance(sourceSubWalletBalance)
                .newSourceWalletBalance(newSourceSubWalletBalance)
                .previousTargetWalletBalance(targetSubWalletBalance)
                .newTargetWalletBalance(newTargetSubWalletBalance)
                .message("Transfer successful")
                .build();

    }
}



