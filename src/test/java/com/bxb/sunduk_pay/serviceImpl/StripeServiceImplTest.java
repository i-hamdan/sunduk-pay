package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.StripeSessionException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {

    private StripeServiceImpl stripeService;

    @BeforeEach
    void setup(){
        stripeService = new StripeServiceImpl("stripe-123");
    }

    @Test
    void testCreateCheckoutSessionForCredit() throws Exception{

        MainWallet mainWallet = mock(MainWallet.class);
        when(mainWallet.getMainWalletId()).thenReturn("main-123");
        WalletWrapper targetWallet = new WalletWrapper(mainWallet);

        Session mockSession = mock(Session.class);

        try (MockedStatic<Session> sessionMock = mockStatic(Session.class)){

                sessionMock
                        .when(()-> Session.create(any(SessionCreateParams.class)))
                        .thenReturn(mockSession);

            Session result = stripeService.createCheckoutSession(
                    "user-123",
                    100.0,
                    TransactionType.CREDIT,
                    targetWallet,
                    null
            );

            assertNotNull(result);

        }
    }

    @Test
    void testCreateCheckoutSessionForDebit() throws Exception{
        MainWallet mainWallet= mock(MainWallet.class);
        when(mainWallet.getMainWalletId()).thenReturn("main-123");
        WalletWrapper sourceWallet = new WalletWrapper(mainWallet);

        Session mockSession = mock(Session.class);

        try(MockedStatic<Session> sessionMock= mockStatic(Session.class)){
            sessionMock
                    .when(()->Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            Session result = stripeService.createCheckoutSession(
                    "user-123",
                    100.0,
                    TransactionType.DEBIT,
                    null,
                    sourceWallet
            );
            assertNotNull(result);
        }
    }

//    @Test
//    void testStripeSessionException() throws RuntimeException{
//        StripeSessionException exception = assertThrows(
//                StripeSessionException.class,()-> stripeService.createCheckoutSession(
//                        "user-123",
//                        20.0,
//                        null,
//                        null,
//                        null
//                )
//        );
//        assertTrue(exception.getMessage().contains("Invalid session type"));
//    }

    @Test
    void testCreateCheckoutSessionSuccess()
            throws Exception {

        try (MockedStatic<Session> sessionMock = mockStatic(Session.class)) {

            sessionMock
                    .when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new StripeException(
                            "Stripe error", null, null, 500, null) {});

            StripeSessionException exception = assertThrows(
                    StripeSessionException.class,
                    () -> stripeService.createCheckoutSession(
                            "user-999",
                            75.0,
                            TransactionType.CREDIT,
                            null,
                            null
                    )
            );

            assertEquals(
                    "Stripe session creation failed. Please try again later.",
                    exception.getMessage()
            );
        }
    }

}