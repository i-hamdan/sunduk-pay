package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.entityBuilder.TransactionBuilder;
import com.bxb.sunduk_pay.event.TransactionEvent;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class TransferService implements WalletOperation {
    private final Validations validations;
    private final TransactionBuilder transactionBuilder;
    private final TransactionRepository transactionRepository;
    private final MainWalletRepository mainWalletRepository;
    private final PaymentService paymentService;
    private final MasterWalletRepository masterWalletRepository;
    private final UserRepository userRepository;

    public TransferService(Validations validations, TransactionBuilder transactionBuilder, TransactionRepository transactionRepository, MainWalletRepository mainWalletRepository, PaymentService paymentService, MasterWalletRepository masterWalletRepository, UserRepository userRepository) {
        this.validations = validations;
        this.transactionBuilder = transactionBuilder;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.paymentService = paymentService;
        this.masterWalletRepository = masterWalletRepository;
        this.userRepository = userRepository;
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
            return handleExternalIncomingTransfer(mainWalletRequest, targetSubWallet, mainWalletRequest.getAmount());
        } else {
            throw new ResourceNotFoundException("both source and target is invalid for this user");
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(MainWalletRequest mainWalletRequest, SubWallet targetSubWallet,Double amount) {
        log.info("Adding amount ${} to MasterWallet for uuid: {}", amount, mainWalletRequest.getUuid());

        MasterWallet masterWallet = validations.getMasterWalletInfo(mainWalletRequest.getUuid());


        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());
        // adding amount on master wallet
            masterWallet.setBalance(masterWallet.getBalance() + amount);
        // aading amount on subwallet when wallet Request have a targetSubWalletId;
if (mainWalletRequest.getSubWalletId().equals(targetSubWallet)){
    targetSubWallet.setBalance(targetSubWallet.getBalance()+amount);
}
// adding amount on main wallet
else {
    mainWallet.setBalance(mainWallet.getBalance()+amount);
}

// Fetch user
        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionId(UUID.randomUUID().toString())
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .status("SUCCESS")
               //.masterWalletId(MasterWallet)
               // .stripePaymentIntentId(paymentIntentId)
                .dateTime(LocalDateTime.now())
                .description("money Added Via Stripe")
                .build();
        transactionRepository.save(transaction);

        mainWallet.getTransactionHistory().add(transaction);

//        MasterWallet.getTransactionHistory().add(transaction);
        masterWalletRepository.save(masterWallet);

        String message = "An amount of $" + amount + " has been added to your MasterWallet successfully.";
        log.info(message);

        return MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(transaction.getTransactionId())
                .message("Transfer Successful")
                .build();
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



