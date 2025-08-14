package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;

    public PaymentServiceImpl(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @Override
    @CircuitBreaker(name = "stripeGateway", fallbackMethod = "paymentFallback")
    public MainWalletResponse createCheckoutSession(String userId, Double amount, TransactionType transactionType) {
        try {
            Session session = stripeService.createCheckoutSession(userId, amount, transactionType);
            return  MainWalletResponse.builder().url(session.getUrl()).build();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    // Fallback method must have same params as main method + Throwable at the end
    public MainWalletResponse paymentFallback(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet, Throwable t) {
        return MainWalletResponse.builder().message("Payment provider unavailable: " + t.getMessage()).build();
    }
}
