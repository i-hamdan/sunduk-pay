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
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
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
        log.info("Performing transfer request for UUID: {}, Request: {}", mainWalletRequest.getUuid(), mainWalletRequest);

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("Fetched user: {}", user);

        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
        log.debug("Fetched main wallet: {}", mainWallet.getMainWalletId());


        WalletWrapper sourceWallet = getWallet(mainWallet, mainWalletRequest.getSourceWalletId());
        Double previousSourceWalletBalance = (sourceWallet == null) ? null : sourceWallet.getBalance();
        log.debug("Source wallet: {}, Previous balance: {}", sourceWallet, previousSourceWalletBalance);

        WalletWrapper targetWallet = getWallet(mainWallet, mainWalletRequest.getTargetWalletId());
        Double previousTargetWalletBalance = (targetWallet == null) ? null : targetWallet.getBalance();
        log.debug("Target wallet: {}, Previous balance: {}", targetWallet, previousTargetWalletBalance);

        boolean sourceExists = (sourceWallet != null);
        boolean targetExists = (targetWallet != null);

        if (sourceExists && targetExists) {
            log.info("Processing internal transfer");
            return handleInternalTransfer(user, mainWallet, mainWalletRequest.getAmount(),
                    sourceWallet, targetWallet, previousSourceWalletBalance, previousTargetWalletBalance);
        }
        else if (sourceExists && !targetExists || sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI|| sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.Bank) {
            log.info("Processing external outgoing transfer");
            return handleExternalOutGoingTransfer(sourceWallet,targetWallet, mainWalletRequest.getAmount(),user);
        }
        else if (!sourceExists && targetExists ||!sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI || !sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.Bank) {
            log.info("Processing external incoming transfer");
            return handleExternalIncomingTransfer(user, mainWalletRequest.getAmount(),targetWallet,sourceWallet);
        }
        else {
            log.error("Both source and target wallets are invalid for UUID: {}", user.getUuid());
            throw new ResourceNotFoundException("both source and target is invalid for this user");
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(User user, Double amount,WalletWrapper targetWallet,WalletWrapper sourceWallet) {
        log.info("Creating checkout session for incoming transfer, Amount: {}", amount);
        return paymentService.createCheckoutSession(user.getUuid(), amount, TransactionType.CREDIT,targetWallet,sourceWallet);
    }

    private MainWalletResponse handleExternalOutGoingTransfer(WalletWrapper sourceSubWallet,WalletWrapper targetWallet, Double amount,User user) {
        log.info("Processing outgoing transfer, Amount: {}", amount);
        validations.validateBalance(sourceSubWallet.getBalance(),amount);
        return paymentService.createCheckoutSession(user.getUuid(), amount, TransactionType.DEBIT,targetWallet,sourceSubWallet);
    }

    public MainWalletResponse handleInternalTransfer(User user, MainWallet mainWallet, Double amount,
                                                     WalletWrapper sourceWallet, WalletWrapper targetWallet,
                                                     Double previousSourceWalletBalance, Double previousTargetWalletBalance) {
        log.info("Starting internal transfer of amount {} from {} to {}", amount, sourceWallet.getId(), targetWallet.getId());

        List<Transaction> transactions = new ArrayList<>();
        validations.validateBalance(sourceWallet.getBalance(), amount);

        sourceWallet.setBalance(sourceWallet.getBalance() - amount);
        Double newSourceWalletBalance = sourceWallet.getBalance();
        Transaction debitTransaction = transactionBuilder.createDebitTransaction(
                sourceWallet.getId(), mainWallet, amount, "Transfer to " + targetWallet.getName());
        transactions.add(debitTransaction);

        targetWallet.setBalance(targetWallet.getBalance() + amount);
        Double newTargetWalletBalance = targetWallet.getBalance();
        Transaction creditTransaction = transactionBuilder.createCreditTransaction(
                targetWallet.getId(), mainWallet, amount, "Received from " + sourceWallet.getName());
        transactions.add(creditTransaction);

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        log.info("Internal transfer completed successfully");
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

        if (walletId.equals(mainWallet.getMainWalletId())) {
            log.debug("Returning main wallet wrapper for wallet ID {}", walletId);
            return new WalletWrapper(mainWallet);
        }
        SubWallet subWallet = validations.validateSubWalletExists(mainWallet, walletId);
        log.debug("Returning sub wallet wrapper for wallet ID {}", walletId);
        return (subWallet != null) ? new WalletWrapper(subWallet) : null;
    }
}
