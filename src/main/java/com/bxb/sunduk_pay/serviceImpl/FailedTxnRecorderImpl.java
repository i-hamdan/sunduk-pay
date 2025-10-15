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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of FailedTxnRecorder service.
 * Responsible for recording failed transactions in the system.
 */
@Service
@RequiredArgsConstructor
public class FailedTxnRecorderImpl implements FailedTxnRecorder {
    /** validations to verify user and wallet information. */
    private final Validations validations;
    /** Repository for Transaction entity. */
    private final TransactionRepository transactionRepository;

    /**
     * Records a failed transaction based on the given MainWalletRequest.
     * Determines the source and target wallets,
     * builds a failed transaction object,
     * saves it to the database,
     * and returns a response indicating failure.
     * @param request the request containing transaction details
     * @return a MainWalletResponse indicating transaction failure.
     */
    @Override
    public MainWalletResponse recordFailedTxn(final MainWalletRequest request) {
        User user = validations.getUserInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(request.getUuid());
        SubWallet sourceSubWallet = validations
                .findSubWalletIfExists(
                        mainWallet.getMainWalletId(),
                        request.getSourceWalletId());
        SubWallet targetSubwallet = validations
                .findSubWalletIfExists(
                        mainWallet.getMainWalletId(),
                        request.getTargetWalletId());

        String fromWallet = null;
        if (mainWallet.getMainWalletId().equals(request.getSourceWalletId())) {
            fromWallet = "Main wallet";
        } else if (
                sourceSubWallet != null
                  && sourceSubWallet.getSubWalletId()
                 .equals(request.getSourceWalletId())) {
            fromWallet = sourceSubWallet.getSubWalletName();
        } else {
            fromWallet = "Some external source";
        }

        String toWallet = null;
        if (mainWallet.getMainWalletId().equals(
                request.getTargetWalletId())) {
            toWallet = "Main wallet";
        } else if (targetSubwallet != null
                && targetSubwallet.getSubWalletId()
                .equals(request.getTargetWalletId())) {
            toWallet = targetSubwallet.getSubWalletName();
        } else {
            toWallet = "Some external target";
        }



        Transaction failedTransaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .description("Transaction failed")
                .dateTime(LocalDateTime.now())
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
