package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.FailedTxnRecorder;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private StripeService stripeService;

    @Mock
    private FailedTxnRecorder failedTxnRecorder;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;


    // -------------------- TEST 1 ---------------------
    @Test
    void shouldReturnCheckoutUrl_WhenStripeSessionCreated() throws Exception {

        // Given
        String userId = "user-123";
        Double amount = 100.0;

        MainWallet mainWallet1 = new MainWallet();
        mainWallet1.setMainWalletId("main-wallet-1");

        WalletWrapper targetWallet = new WalletWrapper(mainWallet1);

        MainWallet mainWallet2 = new MainWallet();
        mainWallet2.setMainWalletId("main-wallet-2");

        WalletWrapper sourceWallet = new WalletWrapper(mainWallet2);

        Session session = new Session();
        session.setUrl("https://checkout.stripe.com/payment");

        when(stripeService.createCheckoutSession(
                userId,
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet))
                .thenReturn(session);

        // WHEN
        MainWalletResponse response = paymentServiceImpl.createCheckoutSession(
                userId,
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet);

        // THEN
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "https://checkout.stripe.com/payment",
                response.getCheckoutUrl());
        Assertions.assertEquals(
                "Session Creating Successful For User: "
                        +userId, response.getMessage());

        verify(stripeService, times(1))
                .createCheckoutSession(any(),any(),any(),any(),any());

    }


    // -------------------- TEST 2 ---------------------

    @Test
    void shouldRuntimeException_WhenStripeFails() throws Exception {

        //Given
        when(stripeService.createCheckoutSession(any(),any(),any(),any(),any()))
                .thenThrow(new RuntimeException("Stripe Fail"));

        Assertions.assertThrows(RuntimeException.class, () ->
                paymentServiceImpl.createCheckoutSession(
                        "user-a",
                        200.0,
                        TransactionType.CREDIT,
                        null,
                        null
                )
        );

    }

    // -------------------- TEST 1 ---------------------
    @Test
    void shouldRecordFailedTraction_WhenFallbackCall(){

        // Given
        String userId = "user-123";
        Double amount = 100.0;

        MainWallet mainWallet1 = new MainWallet();
        mainWallet1.setMainWalletId("main-wallet-1");
        WalletWrapper targetWallet = new WalletWrapper(mainWallet1);

        MainWallet mainWallet2 = new MainWallet();
        mainWallet2.setMainWalletId("main-wallet-2");
        WalletWrapper sourceWallet = new WalletWrapper(mainWallet2);

        Throwable cause = new Throwable("Stripe Api Down");

        // When
        MainWalletResponse mainWalletResponse =
                paymentServiceImpl.paymentFallback(
                userId,
                amount,
                TransactionType.CREDIT,
                targetWallet,
                sourceWallet,
                cause
        );

        // Then
         Assertions.assertNotNull(mainWalletResponse);
         Assertions.assertEquals(
                 "Payment provider unavailable: Stripe Api Down",

                 mainWalletResponse.getMessage());

         Assertions.assertNull(mainWalletResponse.getCheckoutUrl());

         verify(failedTxnRecorder, times(1))
                 .recordFailedTxn(any(MainWalletRequest.class));
    }
}
