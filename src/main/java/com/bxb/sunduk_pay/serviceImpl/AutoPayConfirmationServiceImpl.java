package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.ResourceExpiredException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.AutoPayConfirmation;
import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.AutoPayConfirmationRepository;
import com.bxb.sunduk_pay.service.AutoPayConfirmationService;
import com.bxb.sunduk_pay.service.AutoPaymentService;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.ConfirmationStatus;
import com.bxb.sunduk_pay.validations.Validations;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;

import java.time.LocalDateTime;

/**
 * Service implementation for handling auto-pay confirmation logic.
 * Manages creation of confirmation requests,
 * processing user confirmations/rejections,
 * and sending push notifications to users for high-value auto-payments.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AutoPayConfirmationServiceImpl
        implements AutoPayConfirmationService {
/** Expiry time for confirmation requests in minutes. */
    private static final int EXPIRY_MINUTES = 30;
/** Repository for managing AutoPayConfirmation entities. */
    private final AutoPayConfirmationRepository confirmationRepository;
    /** Service for sending push notifications to users. */
    private final PushNotificationService pushNotificationService;
    /** Service for processing auto-payments after confirmation. */
    private final AutoPaymentService autoPaymentService;
    /** Validation utility for fetching confirmation details. */
    private final Validations validations;

    /**
     * Creates confirmation request for high-value auto-pay.
     */
    @Override
    public void createConfirmation(final Reminder reminder)
            throws FirebaseMessagingException {

        log.info("Creating confirmation for reminder {}",
                reminder.getReminderId());

        // Check if already exists
        boolean exists = confirmationRepository
                .existsByReminderReminderIdAndStatus(
                        reminder.getReminderId(),
                        ConfirmationStatus.PENDING);

        if (exists) {
            log.warn("Confirmation already exists for reminder {}",
                    reminder.getReminderId());
            return;
        }

        AutoPayConfirmation confirmation = AutoPayConfirmation.builder()
                .reminder(reminder)
                .user(reminder.getUser())
                .receiverPhone(reminder.getContactNumber())
                .amount(reminder.getAmount())
                .status(ConfirmationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                .build();

        confirmationRepository.save(confirmation);

        log.info("Confirmation saved with ID {}", confirmation
                .getAutoPayConfirmationId());

        log.info("Sending confirmation notification for reminder {}",
                reminder.getReminderId());
        sendConfirmationNotification(reminder,
                confirmation.getAutoPayConfirmationId());
    }

    /**
     * User confirms payment.
     */
    @Override
    public void confirmPayment(String confirmationId, String userId) {

        AutoPayConfirmation confirmation =
                validations.getConfirmationById(confirmationId);

        validateOwnership(confirmation, userId);

        if (confirmation.getStatus() != ConfirmationStatus.PENDING) {
            throw new ResourceExpiredException("Payment already processed.");
        }

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException(
                    "Confirmation request expired.");
        }

        confirmation.setStatus(ConfirmationStatus.CONFIRMED);
        confirmationRepository.save(confirmation);

        log.info("Confirmation approved {}", confirmationId);

        autoPaymentService.processAutoPayment(
                confirmation.getReminder());
    }

    /**
     * User rejects payment.
     */
    @Override
    public void rejectPayment(String confirmationId, String userId) {

        AutoPayConfirmation confirmation =
                validations.getConfirmationById(confirmationId);

        validateOwnership(confirmation, userId);

        if (confirmation.getStatus() != ConfirmationStatus.PENDING) {
            throw new ResourceNotFoundException("Payment already processed.");
        }

        confirmation.setStatus(ConfirmationStatus.REJECTED);
        confirmationRepository.save(confirmation);

        log.info("Confirmation rejected {}", confirmationId);
    }

    /**
     * Sends push notification to user.
     */
    private void sendConfirmationNotification(
            final Reminder reminder,
            final String confirmationId)
            throws FirebaseMessagingException {

        log.info("Preparing autopay confirm notif for reminderId={}",
                reminder.getReminderId());

        String fcmToken = reminder.getUser().getFcmToken();

        if (fcmToken == null || fcmToken.isBlank()) {
            log.error("FCM token missing for userId={}",
                    reminder.getUser().getUuid());
            throw new ResourceNotFoundException(
                    "User device not registered.");
        }

        log.debug("FCM token found for userId={}",
                reminder.getUser().getUuid());

        String title = "AutoPay Confirmation Required";

        String message = String.format(
                "Approve payment of ₹%.2f to %s",
                reminder.getAmount(),
                reminder.getContactName()
        );

        log.debug("Notification payload created for confirmationId={}",
                confirmationId);

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build();

        Message msg = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("type", "AUTO_PAY_CONFIRMATION")
                .putData("confirmationId", confirmationId)
                .build();

        log.info("Sending FCM notification for confirmationId={}",
                confirmationId);

        try {
            String response = FirebaseMessaging
                    .getInstance()
                    .send(msg);

            log.info("FCM notif sent successfully. responseId={}",
                    response);

        } catch (FirebaseMessagingException ex) {
            log.error("FCM notif failed for confirmationId={}",
                    confirmationId, ex);
            throw ex;
        }
    }

    private void validateOwnership(
            AutoPayConfirmation confirmation,
            String userId) {

        if (!confirmation.getUser().getUuid().equals(userId)) {
            throw new RuntimeException("Invalid confirmation token.");
        }
    }
}
