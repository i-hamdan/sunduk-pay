package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.exception.ChatProcessingException;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import com.bxb.sunduk_pay.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Listener for chat message events from Kafka.
 * Processes incoming chat messages and forwards
 * them to WebSocket clients.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ChatMessageListener {

    /**
     * Executor for handling asynchronous processing of chat messages.
     */
    private final Executor executor = Executors
            .newFixedThreadPool(5);

    /**
     * Messaging template for sending messages to WebSocket clients.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Service for handling chat message operations.
     */
    private final ChatMessageService messageService;

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

        // Handle all messages asynchronously
                processAsync(messageEvent);
    }

    /**
     * Processes the chat message event asynchronously.
     *
     * @param messageEvent the chat message event to be processed
     */
    private void processAsync(final ChatMessageEvent messageEvent) {
        try {
            CompletableFuture
                    .supplyAsync(() -> {
                        log.info(
                "[AsyncThread: {}] Starting message processing...",
                                Thread.currentThread().getName());
                        return messageService.processMessage(messageEvent);
                    }, executor)
                    .thenAccept(response -> {
                        log.info(
                "[ThenAcceptThread: {}] Sending message to user...",
                                Thread.currentThread().getName());
                        sendMessageToWebSocket(response);
                    })
                    .exceptionally(ex -> {
                        log.error("[ErrorThread: {}] Exception: {}",
                            Thread.currentThread().getName(), ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.error("Failed to process chat message asynchronously: "
                    + e.getMessage());
            throw new ChatProcessingException(
                    "Failed to process chat message asynchronously: "
                            + e.getMessage());
        }
    }

    /**
     * Sends the chat message response to the appropriate WebSocket destination.
     *
     * @param response the chat message response to be sent
     */
    private void sendMessageToWebSocket(
            final ChatMessageResponse response) {
        try {
            messagingTemplate.convertAndSend(
                    "/queue/" + response.getReceiverId(),
                    response);
            log.info(
                    "Forwarded chat message to WebSocket destination /queue/{}",
                    response.getReceiverId());
        } catch (Exception e) {
            log.error("Failed to send message to WebSocket: "
                    + e.getMessage());
            throw new ChatProcessingException("unable to forward "
                    + "message to WebSocket. " + e.getMessage());
        }
    }
}
