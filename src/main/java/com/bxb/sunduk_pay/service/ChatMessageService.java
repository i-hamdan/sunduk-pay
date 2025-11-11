package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatAndTransactionUnifiedDTO;
import com.bxb.sunduk_pay.response.ChatMessageResponse;

import java.util.List;

/**
 * Service interface for handling chat messages.
 */
public interface ChatMessageService {
    /**
     * Processes and saves a chat message.
     *
     * @param message the chat message to be saved
     * @return a confirmation string or message ID
     */
    void saveMessage(final ChatMessage message);

    /**
     * Fetches the chat history based on the provided request parameters.
     *
     * @param messageRequest the request containing parameters for
     *                       fetching chat history
     * @return a list of chat message responses
     */
    List<ChatAndTransactionUnifiedDTO> fetchChatHistory(
            final ChatMessageRequest messageRequest);

    /**
     * Processes an incoming chat message event.
     *
     * @param messageEvent the chat message event to be processed
     * @return the response after processing the chat message
     */
    ChatMessageResponse processMessage(
            final ChatMessageEvent messageEvent);

    User getReceiverUserDetails(final String receiverId);
}
