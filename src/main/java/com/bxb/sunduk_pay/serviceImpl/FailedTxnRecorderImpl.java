package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class FailedTxnRecorderImpl implements FailedTxnRecorder {
    private final Validations validations;
    private final TransactionRepository transactionRepository;

    public FailedTxnRecorderImpl(Validations validations, TransactionRepository transactionRepository) {
        this.validations = validations;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public MainWalletResponse recordFailedTxn(MainWalletRequest request) {
        User user = validations.getUserInfo(request.getUuid());
        MainWallet mainWallet = validations.getMainWalletInfo(request.getUuid());
        SubWallet sourceSubWallet = validations.getSubWalletIfExists(mainWallet, request.getSourceWalletId());
        SubWallet targetSubwallet = validations.getSubWalletIfExists(mainWallet, request.getTargetWalletId());

        String fromWallet=null;
        if (mainWallet.getMainWalletId().equals(request.getSourceWalletId())){
            fromWallet="Main wallet";
        } else if (sourceSubWallet!=null && sourceSubWallet.getSubWalletId().equals(request.getSourceWalletId())) {
            fromWallet= sourceSubWallet.getSubWalletName();
        }else{
            fromWallet="Some external source";
        }

        String toWallet=null;
        if (mainWallet.getMainWalletId().equals(request.getTargetWalletId())){
            toWallet="Main wallet";
        } else if (targetSubwallet!=null && targetSubwallet.getSubWalletId().equals(request.getTargetWalletId())) {
            toWallet= targetSubwallet.getSubWalletName();
        }else{
            toWallet="Some external target";
        }



        Transaction failedTransaction = Transaction.builder().transactionId(UUID.randomUUID().toString())
                .user(user)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .description("Transaction failed")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .status("FAILED")
                .fromWallet(fromWallet)
                .fromWalletId(request.getSourceWalletId())
                .toWallet(toWallet)
                .toWalletId(request.getTargetWalletId())
                .build();

        transactionRepository.save(failedTransaction);
        return MainWalletResponse.builder()
                .message("Transaction failed!")
                .build();
    }
}
