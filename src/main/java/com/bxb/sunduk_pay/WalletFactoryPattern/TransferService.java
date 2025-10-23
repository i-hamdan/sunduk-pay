package com.bxb.sunduk_pay.WalletFactoryPattern;

import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.InternalTransferService;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service to handle money transfers between wallets.
 * Supports internal transfers and external
 * transfers via payment gateways.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TransferService implements WalletOperation {

    /**
     * internal transfer service for handling internal wallet transfers.
     */
    private final InternalTransferService internalTransferService;

    /**
     * Validations utility for user and wallet validations.
     */
    private final Validations validations;
    /**
     * MPIN validations utility for MPIN related validations.
     */

    private final MpinValidations mpinValidations;
    /**
     * Payment service for handling external payments.
     */
    private final PaymentService paymentService;

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
     * @param mainWalletRequest request containing source
     *                          , target, amount, and method
     * @return MainWalletResponse with transfer result
     */
    @Override
    public MainWalletResponse perform(
            final MainWalletRequest mainWalletRequest) {
        try {
            if (mainWalletRequest.getAmount() == 0 || mainWalletRequest.getAmount() < 0) {
                throw new InvalidPayloadException("amount cannot be zero or " +
                        "negative");
            }
            log.info("Performing transfer request for UUID: {}, Request: {}",
                    mainWalletRequest.getUuid(), mainWalletRequest);

            User user = validations.getUserInfo(mainWalletRequest.getUuid());
            log.debug("Fetched user: {}", user);

            MainWallet mainWallet = validations.getMainWalletInfo(
                    user.getUuid());
            log.debug("Fetched main wallet: {}",
                    mainWallet.getMainWalletId());


            WalletWrapper sourceWallet = getWallet(mainWallet,
                    mainWalletRequest.getSourceWalletId());
            Double previousSourceWalletBalance = (sourceWallet == null)
                    ? null : sourceWallet.getBalance();
            log.debug("Source wallet: {}, Previous balance: {}",
                    sourceWallet, previousSourceWalletBalance);

            WalletWrapper targetWallet = getWallet(
                    mainWallet, mainWalletRequest.getTargetWalletId());
            Double previousTargetWalletBalance = (
                    targetWallet == null) ? null : targetWallet.getBalance();
            log.debug("Target wallet: {} , Previous balance: {}",
                    targetWallet, previousTargetWalletBalance);

            boolean sourceExists = (sourceWallet != null);
            boolean targetExists = (targetWallet != null);

            if (sourceExists && targetExists) {
                log.info("Processing internal transfer");

                return handleInternalTransfer(user,
                        mainWallet,
                        mainWalletRequest.getAmount(),
                        sourceWallet,
                        targetWallet,
                        previousSourceWalletBalance,
                        previousTargetWalletBalance,
                        mainWalletRequest.getMpin());
            }
            else if (sourceExists && !targetExists) {
                log.info("Processing external outgoing transfer");

                return handleExternalOutGoingTransfer(sourceWallet,
                        targetWallet,
                        mainWalletRequest.getAmount(),
                        user,
                        mainWalletRequest.getMpin()
                );
            }
            else if (!sourceExists && targetExists) {
                log.info("Processing external incoming transfer");

                return handleExternalIncomingTransfer(user,
                        mainWalletRequest.getAmount(),
                        targetWallet,
                        sourceWallet);
            } else {
                log.error("Both source and target "
                                + "wallets are invalid for UUID: {}",
                        user.getUuid());
                throw new InvalidPayloadException(
                        "both sourceId and targetId is invalid for this user");
            }
        } catch (Exception e) {
            log.error("message : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Method for handling external incoming transfer.
     *
     * @param user         the user initiating the transfer,
     * @param amount       the amount to be transferred,
     * @param targetWallet the wallet receiving the funds,
     * @param sourceWallet the wallet from which the
     *                     funds are sent (can be null)
     * @return MainWalletResponse with transfer result
     *
     */
    private MainWalletResponse handleExternalIncomingTransfer(
            final User user,
            final Double amount,
            final WalletWrapper targetWallet,
            final WalletWrapper sourceWallet) {
        log.info(
                "Creating checkout session for incoming transfer, Amount: {}",
                amount);

        return paymentService.createCheckoutSession(user.getUuid(),
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet);
    }

    /**
     * Handle external outgoing transfer.
     *
     * @param sourceSubWallet the wallet from which funds are sent,
     * @param targetWallet    the wallet receiving the funds,
     * @param amount          the amount to be transferred,
     * @param user            the user initiating the transfer
     * @return MainWalletResponse with transfer result
     */
    private MainWalletResponse handleExternalOutGoingTransfer(
            final WalletWrapper sourceSubWallet,
            final WalletWrapper targetWallet,
            final Double amount,
            final User user,
            final String mpin) {
        log.info("Processing outgoing transfer, Amount: {}",
                amount);
        validations.validateBalance(sourceSubWallet.getBalance(),amount);
        /* Validate MPIN for payment */
        mpinValidations.validateMpinForPayment(user.getUuid(),mpin);
        return paymentService.createCheckoutSession(user.getUuid(),
                amount,
                TransactionType.DEBIT,
                targetWallet,
                sourceSubWallet);
    }

    /**
     * Handle internal transfer between main<->subWallet.
     * And subWallet<->subWallet.
     *
     * @param user                        the user initiating the transfer,
     * @param mainWallet                  the user's main wallet,
     * @param amount                      the amount to be transferred,
     * @param sourceWallet                the wallet from which funds are sent,
     * @param targetWallet                the wallet receiving the funds,
     * @param previousSourceWalletBalance the balance of the source
     *                                    wallet before transfer,
     * @param previousTargetWalletBalance the balance of the target
     *                                    wallet before transfer
     * @return MainWalletResponse with transfer result
     *
     */
    public MainWalletResponse handleInternalTransfer(
            final User user,
            final MainWallet mainWallet,
            final Double amount,
            final WalletWrapper sourceWallet,
            final WalletWrapper targetWallet,
            final Double previousSourceWalletBalance,
            final Double previousTargetWalletBalance,
            final String mpin) {
        return internalTransferService
                .doInternalTransfer(user,
                        mainWallet,
                        amount,
                        sourceWallet,
                        targetWallet,
                        previousSourceWalletBalance,
                        previousTargetWalletBalance, mpin);
    }

    /**
     * Get WalletWrapper for mainWallet or subWallet based on walletId.
     *
     * @param mainWallet the main wallet containing sub-wallets,
     * @param walletId   the ID of the wallet to retrieve
     * @return WalletWrapper for the specified walletId,
     * or null if not found
     */
    private WalletWrapper getWallet(
            final MainWallet mainWallet,
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
        log.debug("Requested wallet ID {} does not match MainWallet. "
                        + "Validating sub wallet.",
                walletId);

        SubWallet subWallet = validations.findSubWalletIfExists(
                mainWallet.getMainWalletId(),
                walletId);
        log.debug("Returning sub wallet wrapper for wallet ID {}",
                walletId);
        if (subWallet != null) {
            log.debug("SubWallet found for wallet ID {}."
                            + " Returning SubWallet wrapper.",
                    walletId);
            return new WalletWrapper(subWallet);
        } else {
            log.warn("No Wallet found for wallet ID {} in MainWalletId {}.",
                    walletId, mainWallet.getMainWalletId());
            return null;
        }
    }
}

