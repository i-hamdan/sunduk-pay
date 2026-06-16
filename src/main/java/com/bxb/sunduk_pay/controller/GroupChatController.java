package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.request.GroupChatMessageRequest;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling group chat-related operations.
 * Manages sending group chat messages via WebSocket
 * and forwarding them to Kafka.
 */
@RestController
@Log4j2
@RequiredArgsConstructor
public class GroupChatController {

    /**
     * Kafka template for sending group chat messages.
     */
    private final KafkaTemplate<String, GroupChatEvent> kafkaTemplate;

    /**
     * Utility for generating keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * Mapper for converting group chat message requests to group chat models.
     */
    private final GlobalPotMapper globalPotMapper;

    /**
     * Handles incoming group chat messages from WebSocket clients
     * and forwards them to the Kafka topic "group-chat-messages".
     *
     * @param request the group chat message request payload
     */
    @MessageMapping("/group-chat/sendMessage")
    public void sendMessage(@Payload final GroupChatMessageRequest request) {
        log.info("Received group chat message for pot {}",
                request.getGlobalPotId());

        GroupChatEvent groupChatEvent = globalPotMapper
                .toGroupChatEvent(request);
        log.info("Mapped GroupChatEvent: {}", groupChatEvent);

        String key = generateKeyUtil.getGlobalPotKey(request.getGlobalPotId());
        log.info("Generated key for Kafka message: {}", key);

        kafkaTemplate.send("group-chat-messages",
                key, groupChatEvent);
    }
}
