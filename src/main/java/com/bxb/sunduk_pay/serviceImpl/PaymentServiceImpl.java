package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;
    private final FailedTxnRecorder failedTxnRecorder;

    public PaymentServiceImpl(StripeService stripeService, FailedTxnRecorder failedTxnRecorder) {
        this.stripeService = stripeService;
        this.failedTxnRecorder = failedTxnRecorder;
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
    public MainWalletResponse paymentFallback(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet,Throwable t) {

        log.info("Transfer failed ! Failed transaction will be recorded.");
        MainWalletRequest request=new MainWalletRequest();
        request.setUuid(userId);
        request.setAmount(amount);
        request.setTransactionType(transactionType);
        request.setSourceWalletId((sourceWallet!=null)?sourceWallet.getId():null);
        request.setTargetWalletId((targetWallet!=null)?targetWallet.getId():null);

        failedTxnRecorder.recordFailedTxn(request);
        log.info("failed transaction saved successfully.");
        return MainWalletResponse.builder().message("Payment provider unavailable: " + t.getMessage())
                .checkoutUrl(null)
                .build();
    }
}
