package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;

    public PaymentServiceImpl(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @Override
    @CircuitBreaker(name = "stripeGateway", fallbackMethod = "paymentFallback")
    public MainWalletResponse createCheckoutSession(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet) {
        try {
            Session session = stripeService.createCheckoutSession(userId, amount, transactionType, targetWallet, sourceWallet);
            log.info(String.valueOf(session));

            return MainWalletResponse.builder()
                    .message("Session Creating Successful For User: " + userId )
                    .checkoutUrl(session.getUrl())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("session Creation Failed" +e);
        }

    }

    // Fallback method must have same params as main method + Throwable at the end
    public MainWalletResponse paymentFallback(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet,Throwable t, HttpServletResponse response) {
        return MainWalletResponse.builder().message("Payment provider unavailable: " + t.getMessage())
                .checkoutUrl(null)
                .build();
    }
}
