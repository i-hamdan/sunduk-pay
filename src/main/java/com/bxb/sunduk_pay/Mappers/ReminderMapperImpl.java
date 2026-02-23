package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.response.ReminderResponse;
import com.bxb.sunduk_pay.util.ReminderUtil;
import org.springframework.stereotype.Component;

@Component

public class ReminderMapperImpl implements ReminderMapper {
    /**
     * @param reminder Reminder entity
     * @return ReminderResponse
     */
    @Override
    public ReminderResponse toReminderResponse(final Reminder reminder) {

        ReminderResponse reminderResponse = ReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .amount(reminder.getAmount())
                .duration(reminder.getDuration().toString())
                .startDate(reminder.getStartDate())
                .remark(reminder.getRemark())
                .nextDue(ReminderUtil.calculateDaysUntilNextDue(reminder))
                .contactName(reminder.getContactName())
                .dateTime(reminder.getLocalDateTime())
                .isAvailable(reminder.getIsAvailable())
                .isAutoPayEnabled(reminder.getAutoPayEnabled())
                .build();
        return reminderResponse;
    }
}
