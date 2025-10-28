package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ChatController {
    /**
     * Kafka template for sending chat messages.
     */
    private final KafkaTemplate<String, ChatMessageEvent> kafkaTemplate;
    /**
     * Mapper for converting chat message requests to chat message models.
     */
    private final ChatMessageMapper chatMessageMapper;

    /**
     * Handles incoming chat messages from WebSocket clients
     * and forwards them to the Kafka topic "chat-messages".
     *
     * @param request the chat message request payload
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload final ChatMessageRequest request) {

        ChatMessageEvent messageEvent = chatMessageMapper
                .toMessageEvent(request);

        kafkaTemplate.send("chat-messages", messageEvent);

    }
}
