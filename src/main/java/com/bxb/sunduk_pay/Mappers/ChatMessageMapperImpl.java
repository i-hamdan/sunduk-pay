package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import org.springframework.stereotype.Component;


/**
 * Implementation of the ChatMessageMapper interface for converting between
 * ChatMessageRequest, ChatMessageEvent, and ChatMessage.
 */
@Component
public class ChatMessageMapperImpl implements ChatMessageMapper{
    /**
     * Converts a ChatMessageRequest to a ChatMessageEvent.
     *
     * @param request the chat message request
     * @return the corresponding chat message event
     */
    @Override
    public ChatMessageEvent toMessageEvent(final ChatMessageRequest request) {
        ChatMessageEvent event = new ChatMessageEvent();
        event.setSenderId(request.getSenderId());
        event.setReceiverId(request.getReceiverId());
        event.setContent(request.getContent());
        return event;
    }

    /**
     * Converts a ChatMessageEvent to a ChatMessage.
     *
     * @param event the chat message event
     * @return the corresponding chat message model
     */
    @Override
    public ChatMessage toChatMessage(final ChatMessageEvent event) {
       return ChatMessage.builder()
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .content(event.getContent())
                .build();
    }

    @Override
    public ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getMessageId())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getReceiverId())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .status(chatMessage.getStatus())
                .build();
    }
}
