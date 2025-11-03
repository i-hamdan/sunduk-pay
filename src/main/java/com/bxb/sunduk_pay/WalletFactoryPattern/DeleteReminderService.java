package com.bxb.sunduk_pay.WalletFactoryPattern;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2

/**
 * Service to handle deleting reminders.
 */
public class DeleteReminderService implements WalletOperation{

    /** Repository for accessing reminder data. */
    private final ReminderRepository reminderRepository;
    /**
     * @return
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE_REMINDER;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        try {
            Reminder reminder = reminderRepository.findByReminderId(
                    mainWalletRequest.getReminderId());
            log.info("Deleting reminder with ID: {}",
                    mainWalletRequest.getReminderId());
            if (reminder == null) {
                log.warn("Reminder with ID: {} not found",
                        mainWalletRequest.getReminderId());
                return MainWalletResponse.builder().status("FAILURE").message(
                        "Reminder not found").build();
            }
            reminderRepository.delete(reminder);
            log.info("Reminder with ID: {} deleted successfully",
                    mainWalletRequest.getReminderId());
            return MainWalletResponse.builder().status("SUCCESS").message(
                    "Reminder deleted successfully").build();
        } catch (Exception e) {
            log.error("Unexpected error during reminder deletion: {}",
                    e.getMessage(), e);
            return MainWalletResponse.builder()
                    .status("FAILURE")
                    .message("Unexpected error occurred while deleting " +
                            "reminder")
                    .build();
        }
    }
}
