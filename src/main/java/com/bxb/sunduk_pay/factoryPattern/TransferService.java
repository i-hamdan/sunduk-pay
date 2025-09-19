package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exceptions.InvalidPayloadException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service to handle money transfers between wallets.
 * Supports internal transfers and external transfers via payment gateways.
 */
@Service
@Log4j2
public final class TransferService implements WalletOperation {

    /** Service for performing internal wallet transfers. */
    private final InternalTransferService internalTransferService;

    /** Validations utility for user and wallet checks. */
    private final Validations validations;

    /** Repository to manage transactions. */
    private final TransactionRepository transactionRepository;

    /** Service to handle external payment operations. */
    private final PaymentService paymentService;

    /** Kafka template to send transaction events. */
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /** Repository for main wallet persistence. */
    private final MainWalletRepository mainWalletRepository;

    /** Mapper for transaction entities. */
    private final TransactionMapper transactionMapper;

    /**
     * Constructor for TransferService.
     *
     * @param internalTransferService internal transfer service
     * @param validations validations utility
     * @param transactionRepository transaction repository
     * @param paymentService payment service
     * @param kafkaTemplate kafka template
     * @param mainWalletRepository main wallet repository
     * @param transactionMapper transaction mapper
     */
    public TransferService(
            final InternalTransferService internalTransferService,
            final Validations validations,
            final TransactionRepository transactionRepository,
            final PaymentService paymentService,
            final KafkaTemplate<String, TransactionEvent> kafkaTemplate,
            final MainWalletRepository mainWalletRepository,
            final TransactionMapper transactionMapper
    ) {
        this.internalTransferService = internalTransferService;
        this.validations = validations;
        this.transactionRepository = transactionRepository;
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionMapper = transactionMapper;
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
        log.info("Processing transfer request for UUID: {}, Request: {}",
                mainWalletRequest.getUuid(),
                mainWalletRequest);

        final User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("Fetched user: {}", user);

        final MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
        log.debug("Fetched main wallet: {}", mainWallet.getMainWalletId());

        final WalletWrapper sourceWallet = getWallet(mainWallet, mainWalletRequest.getSourceWalletId());
        final WalletWrapper targetWallet = getWallet(mainWallet, mainWalletRequest.getTargetWalletId());

        final boolean sourceExists = sourceWallet != null;
        final boolean targetExists = targetWallet != null;

        if (sourceExists && targetExists) {
            log.info("Processing internal transfer");
            return handleInternalTransfer(
                    user,
                    mainWallet,
                    mainWalletRequest.getAmount(),
                    sourceWallet,
                    targetWallet,
                    sourceWallet.getBalance(),
                    targetWallet.getBalance()
            );

        } else if (sourceExists && (!targetExists || isExternalPayment(mainWalletRequest))) {
            log.info("Processing external outgoing transfer");
            return handleExternalOutGoingTransfer(
                    sourceWallet,
                    targetWallet,
                    mainWalletRequest.getAmount(),
                    user
            );

        } else if (!sourceExists && (targetExists || isExternalPayment(mainWalletRequest))) {
            log.info("Processing external incoming transfer");
            return handleExternalIncomingTransfer(
                    user,
                    mainWalletRequest.getAmount(),
                    targetWallet,
                    sourceWallet
            );

        } else {
            log.error("Invalid source and target wallets for user UUID: {}", user.getUuid());
            throw new InvalidPayloadException("Both sourceId and targetId are invalid for this user");
        }
    }

    /**
     * Checks if the transfer is via an external payment method.
     *
     * @param request wallet request
     * @return true if external payment, false otherwise
     */
    private boolean isExternalPayment(final MainWalletRequest request) {
        return request.getPaymentMethod() == PaymentMethod.UPI
                || request.getPaymentMethod() == PaymentMethod.BANK;
    }

    /**
     * Handles internal wallet transfers.
     */
    private MainWalletResponse handleInternalTransfer(
            final User user,
            final MainWallet mainWallet,
            final Double amount,
            final WalletWrapper sourceWallet,
            final WalletWrapper targetWallet,
            final Double previousSourceBalance,
            final Double previousTargetBalance
    ) {
        return internalTransferService.doInternalTransfer(
                user,
                mainWallet,
                amount,
                sourceWallet,
                targetWallet,
                previousSourceBalance,
                previousTargetBalance
        );
    }

    /**
     * Handles outgoing transfers via external payment methods.
     */
    private MainWalletResponse handleExternalOutGoingTransfer(
            final WalletWrapper sourceWallet,
            final WalletWrapper targetWallet,
            final Double amount,
            final User user
    ) {
        log.info("Processing outgoing transfer, Amount: {}", amount);
        validations.validateBalance(sourceWallet.getBalance(), amount);
        return paymentService.createCheckoutSession(
                user.getUuid(),
                amount,
                TransactionType.DEBIT,
                targetWallet,
                sourceWallet
        );
    }

    /**
     * Handles incoming transfers via external payment methods.
     */
    private MainWalletResponse handleExternalIncomingTransfer(
            final User user,
            final Double amount,
            final WalletWrapper targetWallet,
            final WalletWrapper sourceWallet
    ) {
        log.info("Creating checkout session for incoming transfer, Amount: {}", amount);
        return paymentService.createCheckoutSession(
                user.getUuid(),
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet
        );
    }

    /**
     * Retrieves a wallet wrapper by ID.
     *
     * @param mainWallet main wallet
     * @param walletId wallet id
     * @return WalletWrapper if found, null otherwise
     */
    private WalletWrapper getWallet(final MainWallet mainWallet, final String walletId) {
        if (walletId == null) {
            log.warn("walletId is null, returning null");
            return null;
        }

        if (walletId.equals(mainWallet.getMainWalletId())) {
            log.debug("Returning main wallet wrapper for wallet ID {}", walletId);
            return new WalletWrapper(mainWallet);
        }

        final SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, walletId);
        if (subWallet != null) {
            log.debug("SubWallet found for wallet ID {}. Returning wrapper.", walletId);
            return new WalletWrapper(subWallet);
        } else {
            log.warn("No wallet found for wallet ID {} in MainWalletId {}", walletId, mainWallet.getMainWalletId());
            return null;
        }
    }
}
