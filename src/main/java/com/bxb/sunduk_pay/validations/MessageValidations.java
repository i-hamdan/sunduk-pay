package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.ChatMessage;

import java.util.List;

/**
 * Interface for validating and processing chat messages.
 */
public interface MessageValidations {
    /**
     * Retrieves chat messages between a sender and receiver from the database.
     *
     * @param senderId   the ID of the sender
     * @param receiverId the ID of the receiver
     * @return a list of chat messages
     */
    List<ChatMessage> getMessagesFromDb(
             String senderId,
            String receiverId);

}
