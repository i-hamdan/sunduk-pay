package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.exception.CannotUpdateWalletException;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
public class UpdateService implements WalletOperation {

    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final Validations validations;

    public UpdateService(MainWalletRepository mainWalletRepository,
                         TransactionRepository transactionRepository,
                         Validations validations) {
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.validations = validations;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    /**
     * Performs update operations on SubWallets.
     * Supported actions: RENAME_POT, GOAL_AMOUNT, GOAL_DATE.
     *
     * @param mainWalletRequest request containing action type and update info
     * @return MainWalletResponse with the result message
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        log.info("Performing action [{}] for UUID [{}]", mainWalletRequest.getActionType(), mainWalletRequest.getUuid());

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("Fetched user details for UUID: {}", user.getUuid());

        validations.getMainWalletInfo(user.getUuid());
        log.debug("Validated main wallet info for user UUID: {}", user.getUuid());

        MainWallet mainWallet = validations.getMainWalletByWalletId(mainWalletRequest.getMainWalletId());
        log.debug("Retrieved MainWallet with ID: {}", mainWallet.getMainWalletId());

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getSubWalletId());

        if (subWallet == null) {
            log.error("SubWallet with ID [{}] not found", mainWalletRequest.getSubWalletId());
            throw new ResourceNotFoundException("SubWallet not found with Id: " + mainWalletRequest.getSubWalletId());
        }

        switch (mainWalletRequest.getActionType()) {
            case RENAME_POT:
                return renameSubWallet(subWallet, mainWallet, mainWalletRequest, user);

            case GOAL_AMOUNT:
                return updateGoalAmount(subWallet, mainWallet, mainWalletRequest);

            case GOAL_DATE:
                return updateGoalDate(subWallet, mainWallet, mainWalletRequest);

            default:
                log.error("Invalid ActionType [{}] provided", mainWalletRequest.getActionType());
                throw new InvalidPayloadException("Please provide a valid ActionType!");
        }
    }

    private MainWalletResponse renameSubWallet(SubWallet subWallet, MainWallet mainWallet,
                                               MainWalletRequest request, User user) {
        log.info("Attempting to rename SubWallet [{}] under MainWallet [{}]",
                subWallet.getSubWalletId(), mainWallet.getMainWalletId());

        List<Transaction> transactions = transactionRepository.findAllByUserAndWallet(user.getUuid(), subWallet.getSubWalletId());
        log.debug("Found [{}] transactions for SubWallet [{}]", transactions.size(), subWallet.getSubWalletId());

        if (!transactions.isEmpty()) {
            log.error("Rename failed! SubWallet [{}] has existing [{}] transactions",
                    subWallet.getSubWalletId(), transactions.size());
            throw new CannotUpdateWalletException("SubWallet [" + subWallet.getSubWalletName() + "] has transactions. Cannot rename.");
        }

        String oldName = subWallet.getSubWalletName();
        subWallet.setSubWalletName(request.getSubWalletName());
        subWallet.setUpdatedAt(LocalDateTime.now());
        mainWalletRepository.save(mainWallet);

        log.info("SubWallet [{}] renamed from [{}] to [{}]", subWallet.getSubWalletId(), oldName, subWallet.getSubWalletName());
        return MainWalletResponse.builder()
                .message("SubWallet previously named [" + oldName + "] was successfully renamed to [" + subWallet.getSubWalletName() + "]")
                .build();
    }

    private MainWalletResponse updateGoalAmount(SubWallet subWallet, MainWallet mainWallet, MainWalletRequest request) {
        log.info("Updating goal amount for SubWallet [{}]", subWallet.getSubWalletId());

        if (request.getTargetBalance() == null || request.getTargetBalance() <= 0) {
            throw new InvalidPayloadException("Target balance cannot be null or zero.");
        }

        Double oldTargetBalance = subWallet.getTargetBalance();
        subWallet.setTargetBalance(request.getTargetBalance());
        subWallet.setUpdatedAt(LocalDateTime.now());
        mainWalletRepository.save(mainWallet);

        log.info("SubWallet [{}] goal amount updated from [{}] to [{}]", subWallet.getSubWalletId(), oldTargetBalance, subWallet.getTargetBalance());
        return MainWalletResponse.builder()
                .message(subWallet.getSubWalletName() + "'s target balance was successfully updated to " + subWallet.getTargetBalance())
                .build();
    }

    private MainWalletResponse updateGoalDate(SubWallet subWallet, MainWallet mainWallet, MainWalletRequest request) {
        log.info("Updating goal date for SubWallet [{}]", subWallet.getSubWalletId());

        LocalDate oldTargetDate = subWallet.getTargetDate();
        subWallet.setTargetDate(request.getTargetDate());
        subWallet.setUpdatedAt(LocalDateTime.now());
        mainWalletRepository.save(mainWallet);

        log.info("SubWallet [{}] goal date updated from [{}] to [{}]", subWallet.getSubWalletId(), oldTargetDate, subWallet.getTargetDate());
        return MainWalletResponse.builder()
                .message(subWallet.getSubWalletName() + "'s target date was successfully updated to " + subWallet.getTargetDate())
                .build();
    }
}
