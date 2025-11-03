package com.bxb.sunduk_pay.WalletFactoryPattern;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 * Service to handle adding reminders.
 */
@Service
@RequiredArgsConstructor
public class AddReminderService implements WalletOperation {

    /** Repository for accessing reminder data. */
    private final ReminderRepository reminderRepository;

    /** Validations utility for input validation and data retrieval.**/
    private final Validations validations;
    /**
     * @return
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.ADD_REMINDER;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

        User user= validations.getUserInfo(mainWalletRequest.getUuid());
        Reminder reminder= Reminder.builder()
                .amount(mainWalletRequest.getAmount())
                .startDate(mainWalletRequest.getStartDate())
                .duration(mainWalletRequest.getDuration())
                .remark(mainWalletRequest.getRemark())
                .user(user)
                .build();

        reminderRepository.save(reminder);
        MainWalletResponse mainWalletResponse= MainWalletResponse.builder()
                .status("SUCCESS")
                .message("Reminder added successfully")
                .build();
        return mainWalletResponse;
    }
}
