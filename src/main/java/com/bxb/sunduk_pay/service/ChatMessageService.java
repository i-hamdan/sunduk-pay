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
     * @param message the chat message to be saved.
     */
    void saveMessage(ChatMessage message);

    /**
     * Fetches the chat history based on the provided request parameters.
     *
     * @param messageRequest the request containing parameters for
     *                       fetching chat history
     * @return a list of chat message responses.
     */
    List<ChatAndTransactionUnifiedDTO> fetchChatHistory(
             ChatMessageRequest messageRequest);

    /**
     * Processes an incoming chat message event.
     *
     * @param messageEvent the chat message event to be processed
     * @return the response after processing the chat message.
     */
    ChatMessageResponse processMessage(
             ChatMessageEvent messageEvent);

    /***
     * Fetches the details of the receiver user based on the receiver ID.
     * @param receiverId the ID of the receiver user whose details are to be fetched
     * @return User.
     */
    User getReceiverUserDetails(String receiverId);
}
