package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.AutoPayConfirmation;
import com.bxb.sunduk_pay.repository.AutoPayConfirmationRepository;
import com.bxb.sunduk_pay.service.AutoPaymentService;
import com.bxb.sunduk_pay.util.ConfirmationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler component responsible for managing auto-pay confirmation tasks.
 * This class can be extended to include scheduled methods that check for
 * pending confirmations, send reminders, or clean up expired confirmation requests.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class AutopayScheduler {

    /**
     * Repository for managing auto-pay confirmation data.
     */
    private final AutoPayConfirmationRepository confirmationRepository;

    /**
     * Service for processing automatic payments based on confirmations.
     */
    private final AutoPaymentService autoPaymentService;

    /**
     * Scheduled method that runs every 3 minutes to check for pending auto-pay
     * confirmations that have expired. If a confirmation has expired, it will
     * automatically process the payment and update the confirmation status.
     */
    @Scheduled(cron = "0 */3 * * * *")
    public void payExpiredConfirmations() {

        LocalDateTime now = LocalDateTime.now();

        List<AutoPayConfirmation> expired = confirmationRepository
                .findAllByStatusAndExpiresAtBefore(
                        ConfirmationStatus.PENDING, now);

        if (expired.isEmpty()) {
            log.info("No expired confirmations found.");
            return;
        }

        log.info("Processing {} expired confirmations.",
                expired.size());

        for (AutoPayConfirmation confirmation : expired) {

            confirmation.setStatus(
                    ConfirmationStatus.EXPIRED_AND_PAID);

            autoPaymentService.processAutoPayment(
                    confirmation.getReminder());
        }

        confirmationRepository.saveAll(expired);

        log.info("Expired confirmations autopaid successfully.");
    }
}
