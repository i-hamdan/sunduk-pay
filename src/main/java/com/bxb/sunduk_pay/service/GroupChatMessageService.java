package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;

/**
 * Service interface for processing group chat messages.
 */
public interface GroupChatMessageService {
    /**
     * Processes an incoming group chat message event.
     *
     * @param event the group chat message event to be processed
     * @return the response after processing the group chat message
     */
    GroupChatMessageResponse processGroupChatMessage(GroupChatEvent event);
}
