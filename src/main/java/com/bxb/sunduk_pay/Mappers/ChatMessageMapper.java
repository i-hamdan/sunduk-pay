package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatMessageResponse;

/**
 * Mapper interface for converting between ChatMessageRequest,
 * ChatMessageEvent, and ChatMessage.
 */
public interface ChatMessageMapper {
    /**
     * Converts a ChatMessageRequest to a ChatMessageEvent.
     *
     * @param request the chat message request
     * @return the corresponding chat message event
     */
    ChatMessageEvent toMessageEvent(final ChatMessageRequest request);

    /**
     * Converts a ChatMessageEvent to a ChatMessage.
     *
     * @param event the chat message event
     * @return the corresponding chat message model
     */
    ChatMessage toChatMessage(final ChatMessageEvent event);

    /**
     * Converts a ChatMessage to a ChatMessageResponse.
     *
     * @param chatMessage the chat message model
     * @param senderNo
     * @param receiverNo
     * @return the corresponding chat message response
     */
    ChatMessageResponse toChatMessageResponse(final ChatMessage chatMessage,
                                              final String senderNo ,
                                              final String receiverNo);

}
