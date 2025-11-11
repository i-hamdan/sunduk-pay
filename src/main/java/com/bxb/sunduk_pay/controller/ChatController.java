package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatAndTransactionUnifiedDTO;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import com.bxb.sunduk_pay.service.ChatMessageService;
import com.bxb.sunduk_pay.service.UserToUserTransferService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.validations.MessageValidations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Controller for handling chat-related operations.
 * Manages sending chat messages via WebSocket
 * and fetching chat history between users.
 */
@Controller
@Log4j2
@RequiredArgsConstructor
@RestController
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
     * Utility for generating keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * Service for chat message operations.
     */
    private final ChatMessageService messageService;

    /**
     * Handles incoming chat messages from WebSocket clients
     * and forwards them to the Kafka topic "chat-messages".
     *
     * @param request the chat message request payload
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload final ChatMessageRequest request) {

        User receivingUser = messageService
                .getReceiverUserDetails(request.getReceiverId());

        request.setReceiverId(receivingUser.getUuid());

        ChatMessageEvent messageEvent = chatMessageMapper
                .toMessageEvent(request);

        String key = generateKeyUtil.generateChatKey(
                request.getSenderId(),
                request.getReceiverId());

        log.info( "Forwarding chat message from {} to {}: {}",
                request.getSenderId(),
                request.getReceiverId(),
                request.getContent());
        kafkaTemplate.send("chat-messages",key, messageEvent);
    }

    /**
     * Fetches the chat history between two users.
     *
     * @param messageRequest the chat message
     *                       request containing sender and receiver IDs
     * @return a ResponseEntity containing a
     * list of ChatAndTransactionUnifiedDTO
     */
    @PostMapping("/chatHistory" )
    public ResponseEntity<List<ChatAndTransactionUnifiedDTO>> fetchChatHistory(
            @RequestBody final ChatMessageRequest messageRequest){
        return ResponseEntity.ok(messageService
                .fetchChatHistory(messageRequest));
    }
}
