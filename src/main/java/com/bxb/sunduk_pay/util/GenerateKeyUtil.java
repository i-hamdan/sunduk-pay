package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating Redis keys for chat history.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class GenerateKeyUtil {

    /** Length of ID part to consider for key generation.
     */
    private static final Integer FOUR = 4;

    private static final Integer SIX = 6;
    /**
     * Generates a Redis key for chat history between two users
     * based on the last 4 digits of their IDs.
     *
     * @param senderId   the ID of the sender
     * @param receiverId the ID of the receiver
     * @return a string representing the Redis key for chat history
     * @throws InvalidPayloadException if either ID
     * is null or has less than 6 digits
     */
    public String generateChatKey(final String senderId,
                                  final String receiverId) {
        if (senderId == null || receiverId == null) {
            throw new InvalidPayloadException(
                    "Sender and Receiver IDs cannot be null");
        }

        // Trim and extract last 4 digits
        String trimmedSender = senderId.trim();
        String trimmedReceiver = receiverId.trim();

        if (trimmedSender.length() < FOUR || trimmedReceiver.length() < FOUR) {
            throw new InvalidPayloadException(
                    "Sender and Receiver IDs must have at least 4 digits");
        }

        String senderLast4 = trimmedSender.substring(
                trimmedSender.length() - SIX);
        String receiverLast4 = trimmedReceiver.substring(
                trimmedReceiver.length() - SIX);

        // Generate consistent key (order-independent)
        String chatKey;
        if (senderLast4.compareTo(receiverLast4) < 0) {
            chatKey = senderLast4 + "_" + receiverLast4;
        } else {
            chatKey = receiverLast4 + "_" + senderLast4;
        }

        log.info(
        "Generated chat key for redis and kafka partition: {}",
                chatKey);
        return chatKey;
    }

    /**
     * Generates a Redis key for transactions between two users
     * by appending a suffix to the chat key.
     *
     * @param senderId   the ID of the sender
     * @param receiverId the ID of the receiver
     * @return a string representing the Redis key for transactions
     */
    public String generateTransactionKey(final String senderId,
                                         final String receiverId) {

        // Reuse existing chat key generation logic
        String chatKey = generateChatKey(senderId, receiverId);

        // Append a meaningful suffix for transactions
        String transactionKey = chatKey + "_txn";

        log.info("Generated transaction key for redis: {}",
                transactionKey);
        return transactionKey;
    }

    /**
     * Generates a Redis key for global pot
     * based on the global pot ID.
     *
     * @param globalPotId the ID of the global pot
     * @return a string representing the Redis key for global pot
     * @throws InvalidPayloadException if the global pot ID is null
     */
    public String getGlobalPotKey(final String globalPotId) {
        if (globalPotId == null) {
            throw new InvalidPayloadException(
                    "Sender and Receiver IDs cannot be null");
        }
        String key = globalPotId.substring(0, SIX);
        return "GLOBAL_POT_" + key;
    }

    /**
     * Generates a Redis key for global pot tiles
     * based on the case category.
     *
     * @param caseCategory the category of the case
     * @return a string representing the Redis key for global pot tiles
     */
    public String getGlobalPotTilesKey(final String caseCategory) {
        if (caseCategory == null) {
          return "key_" + CaseCategory.ALL.toString();
        }
        return "key_" + caseCategory;

    }

    /**
     * Generates a Redis key for group chat
     * based on the global pot ID.
     *
     * @param globalPotId the ID of the global pot
     * @return a string representing the Redis key for group chat
     * @throws InvalidPayloadException if the global pot ID is null
     */
    public String getGroupChatKey(final String globalPotId) {
        if (globalPotId == null) {
          throw new InvalidPayloadException(
                    "Global Pot ID cannot be null");
        }
        String key = globalPotId.substring(0, SIX);
        return "GLOBAL_POT_CHAT_" + key;
    }

    /**
     * Generates a Redis key for group transactions
     * based on the global pot ID.
     *
     * @param globalPotId the ID of the global pot
     * @return a string representing the Redis key for group transactions
     * @throws InvalidPayloadException if the global pot ID is null
     */
    public String getGroupTransactionKey(final String globalPotId) {
        if (globalPotId == null) {
          throw new InvalidPayloadException(
                    "Global Pot ID cannot be null");
        }
        String key = globalPotId.substring(0, SIX);
        return "GLOBAL_POT_TXN_" + key;
    }

}

