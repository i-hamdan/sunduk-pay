package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.UserToUserTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoPaymentServiceImplTest {

    @Mock
    private UserToUserTransferService transferService;

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private AutoPaymentServiceImpl autoPaymentService;

    private Reminder reminder;

    @BeforeEach
    void setup() {
        reminder = new Reminder();

        User user = new User();
        user.setUuid(UUID.randomUUID().toString());

        MainWallet wallet = new MainWallet();
        wallet.setMainWalletId(UUID.randomUUID().toString());

        user.setMainWallet(wallet);

        reminder.setReminderId(UUID.randomUUID().toString());
        reminder.setAmount(500d);
        reminder.setContactNumber("9999999999");
        reminder.setUser(user);
        reminder.setIsPaid(false);
        reminder.setAutoPayInProgress(false);
    }

    @Test
    void shouldProcessAutoPaymentSuccessfully() {

        autoPaymentService.processAutoPayment(reminder);

        verify(reminderRepository, times(2)).save(reminder);

        verify(transferService, times(1))
                .transferBetweenUsers(
                        anyString(),
                        anyString(),
                        anyDouble(),
                        eq("AUTOPAY_REMINDER"),
                        anyString(),
                        eq(reminder.getReminderId()),
                        eq(true)
                );
    }


    @Test
    void shouldSkipIfAlreadyPaid() {

        reminder.setIsPaid(true);

        autoPaymentService.processAutoPayment(reminder);

        verify(transferService, never()).transferBetweenUsers(
                any(), anyString(), any(),
                anyString(), anyString(), anyString(), anyBoolean()
        );

        verify(reminderRepository, never()).save(reminder);
    }

    @Test
    void shouldSkipIfAutoPayInProgress() {

        reminder.setAutoPayInProgress(true);

        autoPaymentService.processAutoPayment(reminder);

        verify(transferService, never()).transferBetweenUsers(
                any(), anyString(), any(),
                anyString(), anyString(), anyString(), anyBoolean()
        );

        verify(reminderRepository, never()).save(reminder);
    }


    @Test
    void shouldUnlockReminderIfTransferFails() {

        doThrow(new RuntimeException("Transfer failed"))
                .when(transferService)
                .transferBetweenUsers(
                        any(), anyString(), any(),
                        anyString(), anyString(), anyString(), anyBoolean()
                );

        autoPaymentService.processAutoPayment(reminder);

        // Locked once + unlocked in catch block
        verify(reminderRepository, atLeastOnce()).save(reminder);
    }
}