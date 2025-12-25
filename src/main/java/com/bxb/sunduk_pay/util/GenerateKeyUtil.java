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
    private final Integer FOUR = 4;

    private final Integer SIX = 6;
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

    public String getGlobalPotKey(String globalPotId){
        if (globalPotId == null ) {
            throw new InvalidPayloadException(
                    "Sender and Receiver IDs cannot be null");
        }
        String key = globalPotId.substring(0, SIX);
        return "GLOBAL_POT_" + key;
    }

    public String getGlobalPotTilesKey(String caseCategory){
        if (caseCategory == null ) {
          return "key_" + CaseCategory.ALL.toString();
        }
        return "key_" + caseCategory;

    }

}

