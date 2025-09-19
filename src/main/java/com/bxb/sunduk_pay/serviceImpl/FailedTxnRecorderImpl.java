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

/**
 * Implementation of FailedTxnRecorder service.
 * Responsible for recording failed transactions in the system.
 */
@Service
public final class FailedTxnRecorderImpl implements FailedTxnRecorder {

    /** Utility class for performing validations and fetching user/wallet info. */
    private final Validations validations;

    /** Repository for storing transaction records. */
    private final TransactionRepository transactionRepository;

    /**
     * Constructs the FailedTxnRecorderImpl.
     *
     * @param validations           utility class for performing validations and fetching user/wallet info
     * @param transactionRepository repository for storing transaction records
     */
    public FailedTxnRecorderImpl(final Validations validations,
                                 final TransactionRepository transactionRepository) {
        this.validations = validations;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Records a failed transaction based on the given MainWalletRequest.
     * Determines the source and target wallets, builds a failed transaction object,
     * saves it to the database, and returns a response indicating failure.
     *
     * @param request the request containing transaction details
     * @return a MainWalletResponse indicating transaction failure
     */
    @Override
    public MainWalletResponse recordFailedTxn(final MainWalletRequest request) {
        final User user = validations.getUserInfo(request.getUuid());
        final MainWallet mainWallet = validations.getMainWalletInfo(request.getUuid());
        final SubWallet sourceSubWallet = validations.getSubWalletIfExists(
                mainWallet, request.getSourceWalletId());
        final SubWallet targetSubwallet = validations.getSubWalletIfExists(
                mainWallet, request.getTargetWalletId());

        final String fromWallet;
        if (mainWallet.getMainWalletId().equals(request.getSourceWalletId())) {
            fromWallet = "Main wallet";
        } else if (sourceSubWallet != null &&
                sourceSubWallet.getSubWalletId().equals(request.getSourceWalletId())) {
            fromWallet = sourceSubWallet.getSubWalletName();
        } else {
            fromWallet = "Some external source";
        }

        final String toWallet;
        if (mainWallet.getMainWalletId().equals(request.getTargetWalletId())) {
            toWallet = "Main wallet";
        } else if (targetSubwallet != null &&
                targetSubwallet.getSubWalletId().equals(request.getTargetWalletId())) {
            toWallet = targetSubwallet.getSubWalletName();
        } else {
            toWallet = "Some external target";
        }

        final Transaction failedTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
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
