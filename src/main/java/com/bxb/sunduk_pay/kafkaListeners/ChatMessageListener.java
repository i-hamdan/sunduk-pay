package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.repository.ChatMessageRepository;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class ChatMessageListener {
    /**
     * Messaging template for sending messages to WebSocket clients.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Mapper for converting chat message events to chat message models.
     */
    private final ChatMessageMapper chatMessageMapper;
    /**
     * Repository for persisting chat messages.
     */
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Consumes chat message events from the "chat-messages" Kafka topic,
     * saves them to the database, and forwards them to the appropriate
     * WebSocket destination.
     *
     * @param messageEvent the chat message event received from Kafka
     */
    @KafkaListener(topics = "chat-messages",
            groupId = "chat-service-group")
    public void consumeChatMessage(final ChatMessageEvent messageEvent) {

        log.info("Received chat message from {} to {}: {}",
                messageEvent.getSenderId(),
                messageEvent.getReceiverId(),
                messageEvent.getContent());

        ChatMessage chatMessage = chatMessageMapper.toChatMessage(messageEvent);
        chatMessage.setMessageId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setStatus("SUCCESS");

        chatMessageRepository.save(chatMessage);

        ChatMessageResponse chatMessageResponse = chatMessageMapper
                .toChatMessageResponse(chatMessage);

        messagingTemplate.convertAndSend(
                "/queue/" + chatMessage.getReceiverId(),
                chatMessageResponse);
    }
}
