package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.GroupChatUnifiedDTO;
import com.bxb.sunduk_pay.service.GroupChatMessageService;
import com.bxb.sunduk_pay.util.ChatDtoDataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Kafka listener for handling group chat messages.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class GroupChatMessageListener {

    /**
     * Executor for handling asynchronous processing of chat messages.
     */
    private final Executor executor = Executors
            .newFixedThreadPool(5);

    /**
     * Service for processing group chat messages.
     */
    private final GroupChatMessageService groupChatMessageService;

    /**
     * Messaging template for sending messages to WebSocket clients.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Consumes group chat messages from the "group-chat-messages" Kafka topic.
     *
     * @param groupChatEvent the group chat event received from Kafka
     * @GroupChatController is responsible for sending messages to this
     * topic.
     */
    @KafkaListener(topics = "group-chat-messages",
            groupId = "group-chat-service-group")
    public void consumeGroupChatMessage(final GroupChatEvent groupChatEvent) {
        log.info(
              "=========== Started processing group chat message ===========");
        log.info("Received group chat message in group {} from {}",
                groupChatEvent.getGlobalPotId(),
                groupChatEvent.getSenderId());

        // Process the message asynchronously
        processGroupMessageAsync(groupChatEvent);
    }

    /**
     * Processes the group chat message asynchronously.
     *
     * @param groupChatEvent the group chat event to process
     */
    private void processGroupMessageAsync(final GroupChatEvent groupChatEvent) {
        CompletableFuture.supplyAsync(() ->
                        groupChatMessageService
                                .processGroupChatMessage(
                                        groupChatEvent), executor)
                .thenAccept(response -> {
                    log.info(
             "[ThenAcceptThread: {}] Sending message to group...",
                            Thread.currentThread().getName());
                    forwardMessageToWebSocket(response);
                })
                .exceptionally(ex -> {
                    log.error(
        "Error processing group chat message in group {} from {}: {}",
                            groupChatEvent.getGlobalPotId(),
                            groupChatEvent.getSenderId(),
                            ex.getMessage());
                    return null;
                });
    }

    /**
     * Forwards the processed group chat message to WebSocket clients.
     *
     * @param response the processed group chat message response
     */
    private void forwardMessageToWebSocket(
            final GroupChatMessageResponse response) {

        GroupChatUnifiedDTO chatDTO = getChatDTO(response);
        // Implementation for forwarding message to WebSocket clients
        messagingTemplate.convertAndSend(
                "/topic/group/" + response.getGlobalPotId(),
                chatDTO);
        log.info(
                "Forwarded group chat message to WebSocket for group {}",
                response.getGlobalPotId());
        log.info(
                "========= Finished processing group chat message ==========");
    }

    /**
     * Converts GroupChatMessageResponse to GroupChatUnifiedDTO.
     *
     * @param response the group chat message response
     * @return the unified DTO representation
     */
    private GroupChatUnifiedDTO getChatDTO(
            final GroupChatMessageResponse response) {
        return GroupChatUnifiedDTO.builder()
                .dataType(ChatDtoDataType.CHAT_MESSAGE)
                .timestamp(response.getTimestamp())
                .data(response)
                .build();
    }

}
