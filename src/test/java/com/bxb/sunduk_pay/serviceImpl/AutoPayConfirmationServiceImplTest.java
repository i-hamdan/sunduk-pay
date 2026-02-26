package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.ResourceExpiredException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.AutoPayConfirmationRepository;
import com.bxb.sunduk_pay.service.AutoPaymentService;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.ConfirmationStatus;
import com.bxb.sunduk_pay.validations.Validations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoPayConfirmationServiceImplTest {

    @Mock
    private AutoPayConfirmationRepository confirmationRepository;

    @Mock
    private PushNotificationService pushNotificationService;

    @Mock
    private AutoPaymentService autoPaymentService;

    @Mock
    private Validations validations;

    @InjectMocks
    private AutoPayConfirmationServiceImpl service;

    private Reminder reminder;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUuid("user-123");
        user.setFcmToken("fcm-token");

        reminder = new Reminder();
        reminder.setReminderId("rem-123");
        reminder.setAmount(500d);
        reminder.setContactName("Ali");
        reminder.setContactNumber("9999999999");
        reminder.setUser(user);
    }

    //CREATE CONFIRMATION SUCCESS
    @Test
    void shouldCreateConfirmation() throws Exception {

        //No existing confirmation
        when(confirmationRepository
                .existsByReminderReminderIdAndStatus(
                        anyString(),
                        eq(ConfirmationStatus.PENDING)))
                .thenReturn(false);

        // Mock save() to simulate DB ID generation
        when(confirmationRepository.save(any(AutoPayConfirmation.class)))
                .thenAnswer(invocation -> {
                    AutoPayConfirmation saved = invocation.getArgument(0);
                    saved.setAutoPayConfirmationId("conf-123");
                    return saved;
                });

        // Mock Firebase static call
        try (MockedStatic<FirebaseMessaging> mocked =
                     mockStatic(FirebaseMessaging.class)) {

            FirebaseMessaging firebaseMessaging =
                    mock(FirebaseMessaging.class);

            mocked.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenReturn("mock-response");

            // Execute
            service.createConfirmation(reminder);

            // Verify save happened
            verify(confirmationRepository)
                    .save(any(AutoPayConfirmation.class));

            // Verify FCM send happened
            verify(firebaseMessaging)
                    .send(any(Message.class));
        }
    }

    //SKIP IF ALREADY EXISTS
    @Test
    void shouldSkipIfConfirmationExists() throws Exception {

        when(confirmationRepository
                .existsByReminderReminderIdAndStatus(
                        anyString(),
                        eq(ConfirmationStatus.PENDING)))
                .thenReturn(true);

        service.createConfirmation(reminder);

        verify(confirmationRepository, never())
                .save(any());
    }

    //CONFIRM PAYMENT SUCCESS
    @Test
    void shouldConfirmPayment() {

        AutoPayConfirmation confirmation =
                AutoPayConfirmation.builder()
                        .autoPayConfirmationId("conf-1")
                        .user(user)
                        .reminder(reminder)
                        .status(ConfirmationStatus.PENDING)
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .build();

        when(validations.getConfirmationById("conf-1"))
                .thenReturn(confirmation);

        service.confirmPayment("conf-1", "user-123");

        assertEquals(ConfirmationStatus.CONFIRMED,
                confirmation.getStatus());

        verify(autoPaymentService)
                .processAutoPayment(reminder);
    }

    //REJECT PAYMENT
    @Test
    void shouldRejectPayment() {

        AutoPayConfirmation confirmation =
                AutoPayConfirmation.builder()
                        .autoPayConfirmationId("conf-1")
                        .user(user)
                        .status(ConfirmationStatus.PENDING)
                        .build();

        when(validations.getConfirmationById("conf-1"))
                .thenReturn(confirmation);

        service.rejectPayment("conf-1", "user-123");

        assertEquals(ConfirmationStatus.REJECTED,
                confirmation.getStatus());
    }

    //EXPIRED CONFIRMATION
    @Test
    void shouldThrowIfExpired() {

        AutoPayConfirmation confirmation =
                AutoPayConfirmation.builder()
                        .autoPayConfirmationId("conf-1")
                        .user(user)
                        .status(ConfirmationStatus.PENDING)
                        .expiresAt(LocalDateTime.now().minusMinutes(5))
                        .build();

        when(validations.getConfirmationById("conf-1"))
                .thenReturn(confirmation);

        assertThrows(ResourceNotFoundException.class, () ->
                service.confirmPayment("conf-1", "user-123"));
    }
}