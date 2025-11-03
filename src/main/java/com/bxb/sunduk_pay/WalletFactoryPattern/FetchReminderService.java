package com.bxb.sunduk_pay.WalletFactoryPattern;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.ReminderResponse;
import com.bxb.sunduk_pay.util.ReminderUtil;
import com.bxb.sunduk_pay.util.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service class for fetching reminders associated
 * with a main wallet.
 */
@Service
@RequiredArgsConstructor
public class FetchReminderService implements WalletOperation{

    private final ReminderRepository reminderRepository;
    /**
     * @return FETCH_REMINDERS request type.
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_REMINDERS;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

            Sort.Direction direction;
            if ("ASC".equalsIgnoreCase(mainWalletRequest.getSortDirection())) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            Pageable pageable = PageRequest.of(mainWalletRequest.getPage(),
                    mainWalletRequest.getSize(),
                    Sort.by(direction, mainWalletRequest.getSortBy()));

        Page<Reminder> reminderPage = reminderRepository.findAll(pageable);
        List<ReminderResponse> reminderResponses = reminderPage.getContent().stream()
                .map(reminder -> ReminderResponse.builder()
                        .reminderId(reminder.getReminderId())
                        .amount(reminder.getAmount())
                        .duration(reminder.getDuration().name())
                        .startDate(reminder.getStartDate())
                        .remark(reminder.getRemark())
                        .nextDue(ReminderUtil.calculateDaysUntilNextDue(reminder))
                        .build())
                .toList();

        return MainWalletResponse.builder()
                .reminders(reminderResponses)
                .build();
    }
}
