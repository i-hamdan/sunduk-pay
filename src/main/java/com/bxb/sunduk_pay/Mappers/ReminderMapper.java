package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.response.ReminderResponse;

/**
 * Mapper interface for converting Reminder entities
 * into ReminderResponse DTOs.
 */
public interface ReminderMapper {

    /**
     * Converts a Reminder entity into a ReminderResponse DTO.
     *
     * @param reminder the Reminder entity
     * @return the corresponding ReminderResponse DTO
     */
    ReminderResponse toReminderResponse(Reminder reminder);
}
