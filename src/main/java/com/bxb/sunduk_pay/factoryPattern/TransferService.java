package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.InternalTransferService;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service to handle money transfers between wallets.
 * Supports internal transfers and external transfers via payment gateways.
 */
@Service
@Log4j2
public class TransferService implements WalletOperation {

    /**internal transfer service for handling internal wallet transfers.*/
    private final InternalTransferService internalTransferService;

    /** Validations utility for user and wallet validations. */
    private final Validations validations;

    /** Payment service for handling external payments. */
    private final PaymentService paymentService;


    public TransferService(final InternalTransferService internalTransferService,
                           final Validations validations,
                           final PaymentService paymentService) {
        this.internalTransferService = internalTransferService;
        this.validations = validations;
        this.paymentService = paymentService;

    }

    /**
     * Returns the request type handled by this service.
     *
     * @return request type TRANSFER_MONEY
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_MONEY;
    }

    /**
     * Performs a money transfer based on source and target wallets.
     *
     * @param mainWalletRequest request containing source, target, amount, and method
     * @return MainWalletResponse with transfer result
     */
    @Override
    public MainWalletResponse perform(final MainWalletRequest mainWalletRequest) {
        try {
            log.info("Performing transfer request for UUID: {}, Request: {}",
                    mainWalletRequest.getUuid(), mainWalletRequest);

            User user = validations.getUserInfo (mainWalletRequest.getUuid());
            log.debug("Fetched user: {}", user);

            MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
            log.debug("Fetched main wallet: {}", mainWallet.getMainWalletId());


            WalletWrapper sourceWallet = getWallet(mainWallet,
                    mainWalletRequest.getSourceWalletId());
            Double previousSourceWalletBalance = (sourceWallet == null) ? null : sourceWallet.getBalance();
            log.debug("Source wallet: {}, Previous balance: {}",
                    sourceWallet, previousSourceWalletBalance);

            WalletWrapper targetWallet = getWallet(mainWallet, mainWalletRequest.getTargetWalletId());
            Double previousTargetWalletBalance = (targetWallet == null) ? null : targetWallet.getBalance();
            log.debug("Target wallet: {} , Previous balance: {}",
                    targetWallet, previousTargetWalletBalance);

            boolean sourceExists = (sourceWallet != null);
            boolean targetExists = (targetWallet != null);

            if (sourceExists && targetExists) {
                log.info("Processing internal transfer");

                /** Both source and target wallets exist - internal transfer. */
                return handleInternalTransfer(user,
                        mainWallet,
                        mainWalletRequest.getAmount(),
                        sourceWallet,
                        targetWallet,
                        previousSourceWalletBalance,
                        previousTargetWalletBalance);
            } else if (sourceExists && !targetExists
                    || sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI
                    || sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.BANK) {
                log.info("Processing external outgoing transfer");

                return handleExternalOutGoingTransfer(sourceWallet,
                        targetWallet,
                        mainWalletRequest.getAmount(),
                        user);
            } else if (!sourceExists && targetExists
                    || !sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI
                    || !sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.BANK) {
                log.info("Processing external incoming transfer");

                return handleExternalIncomingTransfer(user,
                        mainWalletRequest.getAmount(),
                        targetWallet,
                        sourceWallet);
            } else {
                log.error("Both source and target wallets are invalid for UUID: {}",
                        user.getUuid());
                throw new InvalidPayloadException(
                        "both sourceId and targetId is invalid for this user");
            }
        } catch (Exception e) {
            log.error("message : {}", e.getMessage());
            throw e;
        }
    }

    /** Handle external incoming transfer */
    private MainWalletResponse handleExternalIncomingTransfer(final User user,
                                                              final Double amount,
                                                              final WalletWrapper targetWallet,
                                                              final WalletWrapper sourceWallet) {
        log.info("Creating checkout session for incoming transfer, Amount: {}" ,
                amount);
        return paymentService.createCheckoutSession(user.getUuid(),
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet);
    }

    /** Handle external outgoing transfer */
    private MainWalletResponse handleExternalOutGoingTransfer(final WalletWrapper sourceSubWallet,
                                                              final WalletWrapper targetWallet,
                                                              final Double amount,
                                                              final User user) {
        log.info("Processing outgoing transfer, Amount: {}",
                amount);
        validations.validateBalance(sourceSubWallet.getBalance(), amount);
        return paymentService.createCheckoutSession(user.getUuid(),
                amount,
                TransactionType.DEBIT,
                targetWallet,
                sourceSubWallet);
    }

    /** Handle internal transfer between main<->subWallet subWallet<->subWallet*/
    public MainWalletResponse handleInternalTransfer(final User user,
                                                     final MainWallet mainWallet,
                                                     final Double amount,
                                                     final WalletWrapper sourceWallet,
                                                     final WalletWrapper targetWallet,
                                                     final Double previousSourceWalletBalance,
                                                     final Double previousTargetWalletBalance) {
        return internalTransferService.doInternalTransfer(user,
                mainWallet,
                amount,
                sourceWallet,
                targetWallet,
                previousSourceWalletBalance,
                previousTargetWalletBalance);
    }

    /** Get WalletWrapper for mainWallet or subWallet based on walletId.*/
    private WalletWrapper getWallet(final MainWallet mainWallet,
                                    final String walletId) {

        if (walletId == null) {
            log.warn("walletId is null, returning null");
            return null;
        }

        if (walletId.equals(mainWallet.getMainWalletId())) {
            log.debug("Returning main wallet wrapper for wallet ID {}",
                    walletId);
            return new WalletWrapper(mainWallet);
        }
        log.debug("Requested wallet ID {} does not match MainWallet. Validating sub wallet.",
                walletId);

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet,
                walletId);
        log.debug("Returning sub wallet wrapper for wallet ID {}",
                walletId);
        if (subWallet != null) {
            log.debug("SubWallet found for wallet ID {}." +
                    " Returning SubWallet wrapper.",
                    walletId);
            return new WalletWrapper(subWallet);
        } else {
            log.warn("No Wallet found for wallet ID {} in MainWalletId {}.",
                    walletId, mainWallet.getMainWalletId());
            return null;
        }
    }
}