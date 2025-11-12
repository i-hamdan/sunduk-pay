package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.response.ReminderResponse;

public interface ReminderMapper {

    ReminderResponse toReminderResponse(Reminder reminder);
}
