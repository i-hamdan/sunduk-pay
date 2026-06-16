package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ExternalTransferServiceImplTest {

    @InjectMocks
    private ExternalTransferServiceImpl externalTransferService;

    @Mock
    private MasterWalletRepository masterWalletRepository;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private Validations validations;

    // ================= REAL OBJECTS =================
    private MainWalletRequest request;
    private User user;
    private MainWallet mainWallet;
    private MasterWallet masterWallet;

    @BeforeEach
    void setup(){

        request = new MainWalletRequest();
        request.setUuid("user-1");
        request.setAmount(200.0);
        request.setRecipientUpiId("test@upi");
        request.setPaymentTag("UPI_TRANSFER");

        user = new User();
        user.setUuid("user-1");
        user.setTransactionHistory(new ArrayList<>());

        mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MW1");
        mainWallet.setBalance(1000.0);

        masterWallet = new MasterWallet();
        masterWallet.setMasterWalletId("MASTER1");
        masterWallet.setBalance(2000.0);
    }

    // ========================= HAPPY PATH =========================
    @Test
    void shouldHandleUPITransferSuccessfully() {

/// 1
        when(validations.getUserInfo(request.getUuid()))
                .thenReturn(user);

        when(validations.getMainWalletInfo(request.getUuid()))
                .thenReturn(mainWallet);

        when(validations.getMasterWalletInfo(request.getUuid()))
                .thenReturn(masterWallet);

/// 2
        ExternalTransferServiceImpl service;
        MainWalletResponse response =
                externalTransferService.handleUPITransfer(request);

/// 3
        assertEquals(200.0, response.getTransferredAmount());
        assertEquals(800.0, mainWallet.getBalance());
        assertEquals(1800.0, masterWallet.getBalance());

        verify(transactionRepository).saveAll(anyList());
        verify(mainWalletRepository).save(mainWallet);
        verify(masterWalletRepository).save(masterWallet);
    }

    // ================= INVALID UPI =================
    @Test
    void shouldFailWhenRecipientUpiIsInvalid() {

///1
        doThrow(new RuntimeException("Invalid UPI"))
                .when(validations)
                .validateRecipientUpiId(request.getRecipientUpiId());

/// 2
        assertThrows(RuntimeException.class, () ->
                externalTransferService.handleUPITransfer(request)
        );

/// 3
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(mainWalletRepository);
        verifyNoInteractions(masterWalletRepository);
    }

    // ================= INSUFFICIENT BALANCE =================
    @Test
    void shouldFailWhenMainWalletBalanceIsInsufficient() {

/// 1
        when(validations.getUserInfo(request.getUuid()))
                .thenReturn(user);

        when(validations.getMainWalletInfo(request.getUuid()))
                .thenReturn(mainWallet);

        when(validations.getMasterWalletInfo(request.getUuid()))
                .thenReturn(masterWallet);

        doThrow(new RuntimeException("Insufficient balance"))
                .when(validations)
                .validateBalance(1000.0, 200.0);

/// 2
        assertThrows(RuntimeException.class, () ->
                externalTransferService.handleUPITransfer(request)
        );

/// 3
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(mainWalletRepository);
        verifyNoInteractions(masterWalletRepository);
    }

    // ================= TRANSACTION SAVE FAILURE =================
    @Test
    void shouldFailWhenTransactionSaveFails() {

/// 1
        when(validations.getUserInfo(anyString()))
                .thenReturn(user);

        when(validations.getMainWalletInfo(anyString()))
                .thenReturn(mainWallet);

        when(validations.getMasterWalletInfo(anyString()))
                .thenReturn(masterWallet);

        doThrow(new RuntimeException("DB down"))
                .when(transactionRepository)
                .saveAll(anyList());

/// 2
        assertThrows(RuntimeException.class, () ->
                externalTransferService.handleUPITransfer(request)
        );

/// 3
        verify(mainWalletRepository, never()).save(any());
        verify(masterWalletRepository, never()).save(any());
    }

    // ================= MAIN WALLET SAVE FAILURE =================
    @Test
    void shouldFailWhenMainWalletSaveFails() {

/// 1
        when(validations.getUserInfo(anyString()))
                .thenReturn(user);

        when(validations.getMainWalletInfo(anyString()))
                .thenReturn(mainWallet);

        when(validations.getMasterWalletInfo(anyString()))
                .thenReturn(masterWallet);

        doThrow(new RuntimeException("Main wallet DB error"))
                .when(mainWalletRepository)
                .save(mainWallet);

/// 2
        ExternalTransferServiceImpl service;
        assertThrows(RuntimeException.class, () ->
                externalTransferService.handleUPITransfer(request)
        );

/// 3
        verify(masterWalletRepository).save(any());
    }
}


