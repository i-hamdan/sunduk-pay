package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.service.AutoPaymentService;
import com.bxb.sunduk_pay.service.UserToUserTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation responsible for executing automatic payments
 * based on scheduled reminders.
 *
 * <p>
 * This service ensures:
 * <ul>
 *   <li>Reminder is not already paid</li>
 *   <li>No duplicate autopay execution occurs</li>
 *   <li>Reminder is safely locked during processing</li>
 *   <li>Funds are transferred via U2U transfer service</li>
 * </ul>
 * </p>
 */
@Transactional
@Service
@Log4j2
@RequiredArgsConstructor
public class AutoPaymentServiceImpl implements AutoPaymentService {

    /** Service for handling user-to-user transfers. */
    private final UserToUserTransferService userToUserTransferService;

    /** Repository for accessing and updating reminder data. */
    private final ReminderRepository reminderRepository;

    /**
     * Processes automatic payment for a given reminder.
     *
     * <p>
     * This method performs safety checks, locks the reminder to prevent
     * concurrent execution, triggers the payment, and releases the lock.
     * </p>
     *
     * @param reminder the reminder eligible for auto-payment
     */
    @Override
    @Transactional
    public void processAutoPayment(Reminder reminder) {

        log.info("=== AUTOPAY STARTED for reminder {} ===",
                reminder.getReminderId());

        try {

            // ===== SAFETY CHECKS =====
            if (Boolean.TRUE.equals(reminder.getIsPaid())) {
                log.warn("Reminder already paid. Skipping.");
                return;
            }

            if (Boolean.TRUE.equals(reminder.getAutoPayInProgress())) {
                log.warn("Reminder already being processed. Skipping.");
                return;
            }

            // ===== LOCK THE REMINDER =====
            reminder.setAutoPayInProgress(true);
            reminderRepository.save(reminder);

            log.info("Reminder {} locked for autopay",
                    reminder.getReminderId());

            // ===== TRIGGER THE PAYMENT =====
            userToUserTransferService.transferBetweenUsers(
                    reminder.getUser().getUuid(),
                    reminder.getContactNumber(),
                    reminder.getAmount(),
                    "AUTOPAY_REMINDER",
                    //This is the id of the main wallet from which the amount
                    // will be deducted.
                    reminder.getUser()
                            .getMainWallet().getMainWalletId(),
                    reminder.getReminderId(),
                    true
            );

            reminder.setAutoPayInProgress(false);
            reminderRepository.save(reminder);

            log.info("=== AUTOPAY SUCCESS {} ===",
                    reminder.getReminderId());

        } catch (Exception ex) {

            log.error("=== AUTOPAY FAILED {} ===",
                    reminder.getReminderId(), ex);

            reminder.setAutoPayInProgress(false);
            reminderRepository.save(reminder);
        }
    }

}
