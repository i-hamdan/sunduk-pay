package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to handle updating reminders.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateReminderService implements WalletOperation {

    /** Repository for accessing reminder data. */
    private final ReminderRepository reminderRepository;
    /** Validations utility for input validation and data retrieval. **/
    private final Validations validations;

    /**
     * @return the type of request this operation handles,
     * which is UPDATE_REMINDER.
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_REMINDER;
    }

    /**
     * Performs the update reminder operation based on the provided request.
     *
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return MainWalletResponse containing the result of the operation.
     */
    @Override
    public MainWalletResponse perform(
            final MainWalletRequest mainWalletRequest) {
        log.info("Start UpdateReminder: uuid={}, rid={}",
                mainWalletRequest.getUuid(), mainWalletRequest.getReminderId());

        // Validate user and reminder existence
        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("User verified: {}", user.getUuid());

        // Load the reminder to be updated
        Reminder reminder = validations.getReminderById(
                mainWalletRequest.getReminderId());
        log.debug("Reminder loaded: {}", reminder.getReminderId());

        // check if the reminder belongs to the user
        if (!reminder.getUser().getUuid().equals(user.getUuid())) {
            log.warn("Denied: reminder not owned by {}", user.getUuid());
            return MainWalletResponse.builder()
                    .status("FAILED")
                    .message("Unauthorized to update this reminder.")
                    .build();
        }

        // Update the reminder fields based on the request

        if (mainWalletRequest.getAmount() != null) {
            log.info("Set amount: {} -> {}", reminder.getAmount(),
                    mainWalletRequest.getAmount());
            reminder.setAmount(mainWalletRequest.getAmount());
        }

        if (mainWalletRequest.getDuration() != null) {
            log.info("Set duration: {} -> {}", reminder.getDuration(),
                    mainWalletRequest.getDuration());
            reminder.setDuration(mainWalletRequest.getDuration());
        }

        if (mainWalletRequest.getRemark() != null) {
            log.info("Set remark updated");
            reminder.setRemark(mainWalletRequest.getRemark());
        }

        if (mainWalletRequest.getAutoPayEnabled() != null) {
            log.info("Set autoPay: {} -> {}", reminder.getAutoPayEnabled(),
                    mainWalletRequest.getAutoPayEnabled());
            reminder.setAutoPayEnabled(mainWalletRequest.getAutoPayEnabled());
        }

        reminderRepository.save(reminder);
        log.info("Reminder updated: {}", reminder.getReminderId());

        return MainWalletResponse.builder()
                .status("SUCCESS")
                .message("Reminder updated successfully.")
                .build();
    }
}
