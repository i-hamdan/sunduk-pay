package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FailedTxnRecorderImplTest {
    @Mock
    private Validations validations;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FailedTxnRecorderImpl failedTxnRecorderImpl;

    @Test
    void shouldRecordFailedTransactionSuccessfully(){

        // ===================== GIVEN =====================
        MainWalletRequest mainWalletRequest = new MainWalletRequest();
        mainWalletRequest.setUuid("user-123");
        mainWalletRequest.setAmount(500.0);
        mainWalletRequest.setTransactionType(TransactionType.DEBIT);
        mainWalletRequest.setSourceWalletId("MAIN-1");
        mainWalletRequest.setTargetWalletId("SUB-1");


        User user = new User();
        user.setUuid("user-123");

        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MAIN-1");


        SubWallet sourceSubWallet = new SubWallet();
        sourceSubWallet.setSubWalletId("MAIN-2");
        sourceSubWallet.setSubWalletName("Savings");

        // ----- STUBS -----
        when(validations.getUserInfo("user-123"))
                .thenReturn(user);
        when(validations.getMainWalletInfo("user-123"))
                .thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(
                "MAIN-1", "MAIN-1")).
                thenReturn(null);
        when(validations.findSubWalletIfExists(
                "MAIN-1",
                "SUB-1")).
                thenReturn(sourceSubWallet);

        // ===================== WHEN =====================
        MainWalletResponse mainWalletResponse =
                failedTxnRecorderImpl.recordFailedTxn(mainWalletRequest);

        // ===================== THEN =====================
        Assertions.assertNotNull(mainWalletResponse);
        Assertions.assertEquals("Transaction failed!",
                mainWalletResponse.getMessage());
        verify(transactionRepository).save(any(Transaction.class));
    }
}
