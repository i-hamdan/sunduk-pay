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
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class TransferService implements WalletOperation {
    private final Validations validations;
    private final TransactionBuilder transactionBuilder;
    private final TransactionRepository transactionRepository;
private final MainWalletRepository mainWalletRepository;
    public TransferService(Validations validations, TransactionBuilder transactionBuilder, TransactionRepository transactionRepository, MainWalletRepository mainWalletRepository) {
        this.validations = validations;
        this.transactionBuilder = transactionBuilder;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_MONEY;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        User user = validations.getUserInfo(mainWalletRequest.getUuid());

        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());

        SubWallet sourceSubWallet = validations.validateSubWalletExists(mainWallet, mainWalletRequest.getSourceSubWalletId());
        Double sourceSubWalletBalance = sourceSubWallet.getBalance();

        SubWallet targetSubWallet = validations.validateSubWalletExists(mainWallet, mainWalletRequest.getTargetSubWalletId());
        Double targetSubWalletBalance = targetSubWallet.getBalance();


        boolean sourceExists = (sourceSubWallet!=null);
        boolean targetExists = (targetSubWallet!=null);

        if (sourceExists && targetExists) {
            return handleInternalTransfer(sourceSubWallet, targetSubWallet, mainWalletRequest.getAmount(), mainWallet,targetSubWalletBalance,sourceSubWalletBalance);
        }
else if (sourceExists&&!targetExists){
    return handleExternalOutGoingTransfer(sourceSubWallet,mainWalletRequest.getAmount(),mainWalletRequest.getAmount(),sourceSubWalletBalance);
}
else if (!sourceExists&&targetExists){
    return handleExternalIncomingTransfer(mainWallet,targetSubWallet);
        }

else {
     throw new ResourceNotFoundException("both source and target is invalid for this user");
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(MainWallet mainWallet, SubWallet targetSubWallet) {
   return null;
    }


    private MainWalletResponse handleExternalOutGoingTransfer(SubWallet sourceSubWallet, Double amount, Double amount1, Double sourceSubWalletBalance) {
    return null;
    }




    private MainWalletResponse handleInternalTransfer(SubWallet sourceSubWallet, SubWallet targetSubWallet, Double amount, MainWallet mainWallet,double targetSubWalletBalance, double sourceSubWalletBalance  ) {
            List<Transaction> transactions = new ArrayList<>();
            //deducting balance from sourceWallet.
            validations.checkSubWalletAmount(sourceSubWallet, amount);
            sourceSubWallet.setBalance(sourceSubWallet.getBalance() - amount);
            Double newSourceSubWalletBalance = sourceSubWallet.getBalance();
            Transaction debitTransaction = transactionBuilder.createDebitTransaction(sourceSubWallet.getSubWalletId(), mainWallet,amount, "Transfer to subWallet : "+targetSubWallet.getSubWalletName());
            transactions.add(debitTransaction);
            //adding balance to target subWallet.
            targetSubWallet.setBalance(targetSubWallet.getBalance() + amount);
            Double newTargetSubWalletBalance = targetSubWallet.getBalance();
            Transaction creditTransaction = transactionBuilder.createCreditTransaction(targetSubWallet.getSubWalletId(), mainWallet, amount, "Received from subWallet : "+sourceSubWallet.getSubWalletName());
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



