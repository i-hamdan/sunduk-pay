package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;

import com.bxb.sunduk_pay.exception.CannotUpdateWalletException;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.ActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class UpdateService implements WalletOperation {
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final Validations validations;

    public UpdateService(MainWalletRepository mainWalletRepository, TransactionRepository transactionRepository, UserRepository userRepository, WalletMapper walletMapper, Validations validations) {
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
        this.validations = validations;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

        log.info("Performing action [{}]",
                mainWalletRequest.getActionType());

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("Fetched user details for UUID: {}", user.getUuid());

        validations.getMainWalletInfo(user.getUuid());
        log.debug("Validated main wallet info for user UUID: {}", user.getUuid());

        MainWallet mainWallet = validations.getMainWalletByWalletId(mainWalletRequest.getMainWalletId());
        log.debug("Retrieved MainWallet with ID: {}", mainWallet.getMainWalletId());

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getSubWalletId());

        String oldName = (subWallet != null) ? subWallet.getSubWalletName() : null;

        if (mainWalletRequest.getActionType() == ActionType.RENAME_POT) {
            if (subWallet != null) {
                log.info("Attempting to rename SubWallet [{}] under MainWallet [{}]",
                        subWallet.getSubWalletId(), mainWallet.getMainWalletId());

                List<Transaction> allSubWalletTransactions = transactionRepository.findAllByUserAndWallet(user.getUuid(), subWallet.getSubWalletId());
                log.debug("Found [{}] transactions for SubWallet [{}]",
                        allSubWalletTransactions.size(), subWallet.getSubWalletId());

                if (allSubWalletTransactions.isEmpty()) {
                    subWallet.setSubWalletName(mainWalletRequest.getSubWalletName());
                    mainWalletRepository.save(mainWallet);
                    log.info("SubWallet [{}] successfully renamed from [{}] to [{}]",
                            subWallet.getSubWalletId(), oldName, subWallet.getSubWalletName());

                } else {
                    log.error("Rename failed! SubWallet [{}] has existing [{}] transactions",
                            subWallet.getSubWalletId(), allSubWalletTransactions.size());
                    throw new CannotUpdateWalletException("The particular subWallet with name : " + subWallet.getSubWalletName() + " is involved in transactions! Cannot update this wallet.");
                }

            } else {
                log.error("Rename failed! SubWallet with ID [{}] not found", mainWalletRequest.getSubWalletId());
                throw new ResourceNotFoundException("Cannot find subWallet with subWallet Id : " + mainWalletRequest.getSubWalletId());
            }

            return MainWalletResponse.builder().message("SubWallet previously named as : " + oldName + " was successfully renamed to " + subWallet.getSubWalletName()).build();
        }
        log.error("Invalid ActionType [{}] provided in request", mainWalletRequest.getActionType());

        throw new InvalidPayloadException("Please provide a valid ActionType!");
    }


}
