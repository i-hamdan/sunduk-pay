package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.exception.InactiveGlobalWalletException;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.DebitGlobalPotWalletService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebitGlobalPotWalletServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalWalletRepository globalWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DebitGlobalPotWalletService service;

    @Test
    void testDebitGlobalPot(){

        GlobalPotRequestType type = service.getGlobalPotRequestType();
        assertEquals(GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET,type
        ,"Amount should be debited from global wallet.");
    }

    @Test
    void testDebitGlobalPotSuccessfully() throws IOException {

        User admin = User.builder()
                .uuid("admin-123")
                .userRole(UserRoles.GLOBALPOT_ADMIN)
                .build();

        GlobalWallet globalWallet = GlobalWallet.builder()
                .globalWalletId("wallet-123")
                .balance(1000.0)
                .isActive(true)
                .build();
        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("pot-123")
                .globalWallet(globalWallet)
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-123")
                .globalPotId("pot-123")
                .targetAmount(500.0)
                .globalPotRequestType(GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET)
                .build();

        when(validations.getUserInfo("admin-123"))
                .thenReturn(admin);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin);

        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);

        doNothing().when(validations)
                .validateTargetBalance(1000.0, 500.0);

        GlobalPotResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("Amount-500.0 debited from wallet-123", response.getMessage());

        Mockito.verify(globalWalletRepository).save(globalWallet);
        Mockito.verify(transactionRepository).save(ArgumentMatchers.any(Transaction.class));
    }


    @Test
    void testThrowExceptionIfWalletIsInactive() {

        User admin = User.builder()
                .uuid("admin-123")
                .userRole(UserRoles.GLOBALPOT_ADMIN)
                .build();

        GlobalWallet globalWallet = GlobalWallet.builder()
                .globalWalletId("wallet-123")
                .balance(1000.0)
                .isActive(false)
                .build();

        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("pot-123")
                .globalWallet(globalWallet)
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-123")
                .globalPotId("pot-123")
                .targetAmount(500.0)
                .globalPotRequestType(GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET)
                .build();

    when(validations.getUserInfo("admin-123"))
            .thenReturn(admin);

    doNothing().when(globalPotValidations)
            .validateAdmin(admin);

    when(globalPotValidations.getGlobalPot("pot-123"))
            .thenReturn(globalPot);

    assertThrows(
            InactiveGlobalWalletException .class,
                () -> service.perform(request)
            );

    verify(globalWalletRepository, never()).save(any());
    verify(transactionRepository, never()).save(any());
    }

    @Test
    void test_IfInsufficientBalance() {

        User admin = User.builder()
                .uuid("admin-123")
                .userRole(UserRoles.GLOBALPOT_ADMIN)
                .build();

        GlobalWallet globalWallet = GlobalWallet.builder()
                .globalWalletId("wallet-123")
                .balance(100.0)
                .isActive(true)
                .build();
        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("pot-123")
                .globalWallet(globalWallet)
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-123")
                .globalPotId("pot-123")
                .targetAmount(500.0)
                .globalPotRequestType(GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET)
                .build();

        when(validations.getUserInfo("admin-123"))
                .thenReturn(admin);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin);

        when(globalPotValidations.getGlobalPot("pot-123")
        ).thenReturn(globalPot);

        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(validations)
                .validateTargetBalance(100.0, 500.0);

        assertThrows(
                InsufficientBalanceException.class,
                () -> service.perform(request)
        );

        verify(globalWalletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }


}