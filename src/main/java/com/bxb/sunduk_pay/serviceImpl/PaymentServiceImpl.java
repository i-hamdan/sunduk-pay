package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Implementation of PaymentService to handle payments via Stripe.
 * Integrates with Stripe for checkout session creation and uses
 * circuit breaker pattern to record failed transactions automatically.
 */
@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {

    /** Service for interacting with Stripe API */
    private final StripeService stripeService;

    /** Service for recording failed transactions */
    private final FailedTxnRecorder failedTxnRecorder;

    /**
     * Constructs PaymentServiceImpl with required dependencies.
     *
     * @param stripeService       service for interacting with Stripe API
     * @param failedTxnRecorder   service for recording failed transactions
     */
    public PaymentServiceImpl(final StripeService stripeService,
                              final FailedTxnRecorder failedTxnRecorder) {
        this.stripeService = stripeService;
        this.failedTxnRecorder = failedTxnRecorder;
    }

    /**
     * Creates a Stripe checkout session for a user payment.
     * Applies circuit breaker to fallback if Stripe is unavailable.
     *
     * @param userId          the user initiating the payment
     * @param amount          the payment amount
     * @param transactionType type of transaction (DEBIT/CREDIT)
     * @param targetWallet    target wallet for credit
     * @param sourceWallet    source wallet for debit
     * @return MainWalletResponse containing checkout URL or error
     */
    @Override
    @CircuitBreaker(name = "stripeGateway", fallbackMethod = "paymentFallback")
    public MainWalletResponse createCheckoutSession(final String userId,
                                                    final Double amount,
                                                    final TransactionType transactionType,
                                                    final WalletWrapper targetWallet,
                                                    final WalletWrapper sourceWallet) {
        try {
            Session session = stripeService.createCheckoutSession(
                    userId, amount, transactionType, targetWallet, sourceWallet
            );
            log.info("Stripe session created: {}", session);

            return MainWalletResponse.builder()
                    .message("Session created successfully for user: " + userId)
                    .checkoutUrl(session.getUrl())
                    .build();
        } catch (Exception e) {
            log.error("Stripe session creation failed for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Session creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method invoked by Resilience4j circuit breaker when Stripe fails.
     * Records the failed transaction for auditing and user feedback.
     *
     * @param userId          the user initiating the payment
     * @param amount          the payment amount
     * @param transactionType type of transaction
     * @param targetWallet    target wallet for credit
     * @param sourceWallet    source wallet for debit
     * @param t               the exception that caused fallback
     * @return MainWalletResponse with failure message
     */
    public MainWalletResponse paymentFallback(final String userId,
                                              final Double amount,
                                              final TransactionType transactionType,
                                              final WalletWrapper targetWallet,
                                              final WalletWrapper sourceWallet,
                                              final Throwable t) {
        log.warn("Payment failed! Recording failed transaction for user {}", userId);

        MainWalletRequest request = new MainWalletRequest();
        request.setUuid(userId);
        request.setAmount(amount);
        request.setTransactionType(transactionType);
        request.setSourceWalletId(sourceWallet != null ? sourceWallet.getId() : null);
        request.setTargetWalletId(targetWallet != null ? targetWallet.getId() : null);

        failedTxnRecorder.recordFailedTxn(request);
        log.info("Failed transaction recorded successfully.");

        return MainWalletResponse.builder()
                .message("Payment provider unavailable: " + t.getMessage())
                .checkoutUrl(null)
                .build();
    }
}
