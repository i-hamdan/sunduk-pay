package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;

import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatMessageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Implementation of the ChatMessageMapper interface for converting between
 * ChatMessageRequest, ChatMessageEvent, and ChatMessage.
 */
@Component
@RequiredArgsConstructor
public class ChatMessageMapperImpl implements ChatMessageMapper {


    /**
     * Mapper for converting transaction-related data.
     */
    private final TransactionMapper transactionMapper;

    /**
     * Converts a ChatMessageRequest to a ChatMessageEvent.
     *
     * @param request the chat message request
     * @return the corresponding chat message event
     */
    @Override
    public ChatMessageEvent toMessageEvent(
            final ChatMessageRequest request) {
        ChatMessageEvent event = new ChatMessageEvent();
        event.setSenderId(request.getSenderId());
        event.setReceiverId(request.getReceiverId());
        event.setContent(request.getContent());
        event.setIsBlocked(request.getIsBlocked());
        event.setImagePath(request.getImagePath());
        event.setIsImage(request.getIsImage());
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
        ChatMessage.ChatMessageBuilder builder = ChatMessage.builder()
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .status("SUCCESS")
                .content(event.getContent())
                .isBlocked(event.getIsBlocked())
                .imagePath(event.getImagePath())
                .isImage(event.getIsImage());
        return builder.build();
    }

    /**
     * Converts a ChatMessage to a ChatMessageResponse.
     *
     * @param chatMessage the chat message model
     * @param senderNo
     * @param receiverNo
     * @return the corresponding chat message response
     */
    @Override
    public ChatMessageResponse toChatMessageResponse(
            final ChatMessage chatMessage,
            final String senderNo,
            final String receiverNo) {
        String timestamp = chatMessage.getTimestamp().toString();
        String[] parts = timestamp.split("T");
        String datePart = parts[0]; // e.g. "2025-09-03"
        String timePart = parts.length > 1
                ? parts[1].split("\\.")[0] : ""; // e.g. "14:40:49"

        // Format date: "3 Sep 2025"
        String formattedDate = "";
        try {
            java.time.LocalDate localDate = java.
                    time.LocalDate.parse(datePart);
            formattedDate = localDate.format(java.
                    time.format.DateTimeFormatter.ofPattern("d MMM yyyy"));
        } catch (Exception e) {
            formattedDate = datePart; // fallback if parsing fails
        }
        // Format time: "02:40:49 PM"
        String formattedTime = "";
        try {
            java.time.LocalTime localTime = java.time.LocalTime.parse(timePart);
            formattedTime = localTime.format(
                    java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        } catch (Exception e) {
            formattedTime = timePart; // fallback if parsing fails
        }
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getMessageId())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getReceiverId())
                .content(chatMessage.getContent())
                .senderPhoneNumber(senderNo)
                .receiverPhoneNumber(receiverNo)
                .isBlocked(chatMessage.isBlocked())
                .date(formattedDate)
                .time(formattedTime)
                .status(chatMessage.getStatus())
                .imagePath(chatMessage.getImagePath())
                .isImage(chatMessage.getIsImage())
                .build();

    }

}
