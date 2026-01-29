package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.exception.InactiveGlobalWalletException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.GlobalPotTransactionRepository;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service to handle debiting funds from a global pot wallet.
 */
@Service
@AllArgsConstructor
public class DebitGlobalPotWalletService implements GlobalPotOperation {

    /** validations class for subWallet.*/
    private final Validations validations;

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;

    /** Repository for global pot data. */
    private final GlobalPotRepository globalPotRepository;

    /** Repository for global wallet data. */
    private final GlobalWalletRepository globalWalletRepository;


    private final GlobalPotTransactionRepository globalPotTransactionRepository;


    /**
     * Returns the type of global pot request this operation handles.
     *
     * @return GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET;
    }

    /**
     * Performs the debit operation on the global pot wallet.
     *
     * @param request the GlobalPotRequest containing debit details
     * @return GlobalPotResponse indicating the result of the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        User admin = validations.getUserInfo(request.getAdminUuid());

        GlobalPot globalPot = globalPotValidations.getGlobalPot(
                request.getGlobalPotId());

        globalPotValidations.validateAdmin(admin,globalPot);

        GlobalWallet globalWallet = globalPot.getGlobalWallet();

        if (!globalWallet.getIsActive()) {
            throw new InactiveGlobalWalletException(
                    "Global wallet is inactive");
        }

        Double amount = request.getTargetAmount();

validations.validateBalance(globalWallet.getBalance(), amount);

        globalWallet.setBalance(globalWallet.getBalance() - amount);

        globalPot.setCurrentBalance(globalPot.getCurrentBalance() - amount);

        GlobalPotTransaction globalPotTransaction = GlobalPotTransaction
                .builder()
                .globalPot(globalPot)
                .globalWallet(globalWallet)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .description("Admin debited amount-" + amount
                        + " from Global Pot")
                .user(admin)
                .dateTime(LocalDateTime.now()).build();

        globalPotTransactionRepository.save(globalPotTransaction);
        globalPotRepository.save(globalPot);
        globalWalletRepository.save(globalWallet);


        return GlobalPotResponse.builder()
                .message("Amount-" + amount + " debited from "
                        + globalWallet.getGlobalWalletId())
                .currentBalance(globalWallet.getBalance())
                .status("SUCCESS")
                .build();
    }


}
