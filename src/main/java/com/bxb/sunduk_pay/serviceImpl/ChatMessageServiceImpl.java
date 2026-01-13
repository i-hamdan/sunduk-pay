package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;


import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.ChatProcessingException;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WebSocketUserNotFoundException;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ChatMessageRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.ChatMessageRequest;
import com.bxb.sunduk_pay.response.ChatAndTransactionUnifiedDTO;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.ChatMessageService;
import com.bxb.sunduk_pay.util.ChatDtoDataType;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.validations.MessageValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ChatMessageService to handle chat message operations.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    /** Start index for masking phone numbers in logs.
    */
    private static final int SUBSTRING_START_INDEX = 9;
    /** End index for masking phone numbers in logs.
    */
    private static final int SUBSTRING_END_INDEX = 13;
   /** Duration in hours for Redis key expiration.
    */
    private static final int DURATION_HOURS = 24;
    /**
     * Validations for message processing.
     */
    private final MessageValidations messageValidations;


    /**
     * Redis template for interacting with messages stored in redis.
     */
    private final RedisTemplate<String, ChatMessage> chatMessageRedisTemplate;

    /**
     * Redis template for interacting with transactions stored in redis.
     */
    private final RedisTemplate<String, TransactionResponse>
            transactionRedisTemplate;

    /**
     * Utility for generating Redis keys.
     */
    private final GenerateKeyUtil generateKeyUtil;
    /**
     * Mapper for converting between ChatMessageEvent and ChatMessage.
     */
    private final ChatMessageMapper chatMessageMapper;
    /**
     * Repository for persisting chat messages.
     */
    private final ChatMessageRepository chatMessageRepository;
    /**
     * Validations for various operations.
     */
    private final Validations validations;
    /**
     * Repository for persisting transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Mapper for converting between Transaction and its DTOs.
     */
    private final TransactionMapper transactionMapper;


    /**
     * Processes an incoming chat message event.
     *
     * @param messageEvent the chat message event to be processed
     * @return the response containing details of the processed chat message
     */
    @Override
    public ChatMessageResponse processMessage(
            final ChatMessageEvent messageEvent) {
        log.info("Processing chat message from {} to {}: {}",
                messageEvent.getSenderId(),
                messageEvent.getReceiverId(),
                messageEvent.getContent());
        try {
            ChatMessage chatMessage = chatMessageMapper
                    .toChatMessage(messageEvent);
            chatMessage.setMessageId(UUID.randomUUID().toString());
            chatMessage.setTimestamp(LocalDateTime.now());

            // Save the chat message in redis
            saveMessage(chatMessage);

            // Persist the chat message in the database
            chatMessageRepository.save(chatMessage);
            log.info("saved chat message with ID: {}",
                    chatMessage.getMessageId());

            return chatMessageMapper
                    .toChatMessageResponse(chatMessage,
                            getPhoneNumberbyUuid(chatMessage.getSenderId()),
                            getPhoneNumberbyUuid(chatMessage.getReceiverId()));

        } catch (RedisOperationException e) {
            log.error("Failed to process message due to Redis error: "
                    + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error processing chat message: " + e.getMessage());
            throw new ChatProcessingException(
                    "Failed to process chat message: "
                            + e.getMessage());
        }
    }

    /**
     * Saves a chat message to Redis.
     * @param message the chat message to be saved.
     */
    @Override
    public void saveMessage(final ChatMessage message) {

        try {

            // Generate Redis key based on sender and receiver IDs
            String key = generateKeyUtil.generateChatKey(message.getSenderId(),
                    message.getReceiverId());

            // Check if Redis key exists
            if (Boolean.FALSE.equals(chatMessageRedisTemplate.hasKey(key))) {
                log.warn(
    "Redis key expired or not found for key: {}. Reloading from DB...",
                        key);

                // Fetch existing chat history from DB
                List<ChatMessage> dbMessages = messageValidations
                        .getMessagesFromDb(
                                message.getSenderId(),
                                message.getReceiverId());

                if (!dbMessages.isEmpty()) {
                    chatMessageRedisTemplate.opsForList().rightPushAll(
                            key, dbMessages);
                    log.info(
                            "Rehydrated Redis with {} old messages for key: {}",
                            dbMessages.size(), key);
                }
            }
            // Save the message to Redis list
            chatMessageRedisTemplate.opsForList().rightPush(key, message);
            // Set expiration time for the key if not already set
            chatMessageRedisTemplate.expire(key, Duration.ofHours(
                    DURATION_HOURS));

            log.info("Saved chat message to Redis with key: {}", key);
        } catch (Exception e) {
            log.error("Error saving message to Redis", e);
            throw new RedisOperationException(
                    "Failed to save message to Redis , an "
                            + "error occurred: " + e.getMessage());
        }
    }

    /**
     * Fetches chat history based on the provided request parameters.
     *
     * @param messageRequest the request containing parameters for
     *                       fetching chat history
     * @return a list of chat message responses
     */
    @Override
    public List<ChatAndTransactionUnifiedDTO> fetchChatHistory(
            final ChatMessageRequest messageRequest) {

        User receiver = getReceiverUserDetails(messageRequest.getReceiverId());

        log.info(
    "Fetching chat & transaction history for Sender: {} Receiver: {}",
                messageRequest.getSenderId(), receiver.getUuid());

        User sendingUser = validations.getUserInfo(
                messageRequest.getSenderId());

        User receivingUser =
                validations.getUserInfo(receiver.getUuid());

        String chatKey = generateKeyUtil.generateChatKey(
                messageRequest.getSenderId(),
                receiver.getUuid());

        String transactionKey = generateKeyUtil.generateTransactionKey(
                messageRequest.getSenderId(),
                receiver.getUuid());

        log.debug(
          "Generated Redis keys -> chatKey: {}, transactionKey: {}",
                chatKey, transactionKey);


        // 1. Load chat messages from Redis
        List<ChatMessage> chatMessages = chatMessageRedisTemplate.opsForList()
                .range(chatKey, 0, -1);

        if (chatMessages == null || chatMessages.isEmpty()) {
            log.info("Chat not found in Redis. Fetching from DB...");
            chatMessages = messageValidations.getMessagesFromDb(
                    messageRequest.getSenderId(),
                    receiver.getUuid()
            );

            if (!chatMessages.isEmpty()) {
                chatMessageRedisTemplate.opsForList().rightPushAll(
                        chatKey, chatMessages);

                chatMessageRedisTemplate.expire(chatKey,
                        Duration.ofHours(DURATION_HOURS));
                log.info("Chat loaded into Redis & TTL set (24 hours)");

            }

        } else {
            log.info("Chat loaded from Redis. Total messages: {}",
                    chatMessages.size());
        }

        List<TransactionResponse> transactions = transactionRedisTemplate
                .opsForList().range(transactionKey, 0, -1);

        if (transactions == null || transactions.isEmpty()) {
            log.info("Transactions not found in Redis. Fetching from DB...");

            List<Transaction> dbTransactions = transactionRepository
                    .findChatTransactions(
                            sendingUser.getPhoneNumber(),
                            receivingUser.getPhoneNumber(),
                            PaymentMethod.PHONE_NUMBER);

            transactions = dbTransactions.stream().map(
                    transactionMapper::toTransactionResponse).toList();

            if (!transactions.isEmpty()) {
                transactionRedisTemplate.opsForList().rightPushAll(
                        transactionKey,
                        transactions);

                transactionRedisTemplate.expire(transactionKey,
                        Duration.ofHours(DURATION_HOURS));
                log.info("Transactions cached & TTL set (24 hours)");

            }
        } else {
            log.info("Transactions loaded from Redis. Total: {}",
                    transactions.size());
        }

        List<TransactionResponse> senderTransactions =
                transactions.stream().filter(
                                txn -> txn.getUuid()
                                        .equals(messageRequest.getSenderId()))
                        .toList();
        log.debug("Filtered sender-initiated transactions: {}",
                senderTransactions.size());


        // Convert chat messages to unified DTO
        List<ChatAndTransactionUnifiedDTO> chatItems = chatMessages.stream()
                .map(msg -> ChatAndTransactionUnifiedDTO.builder()
                        .type(ChatDtoDataType.CHAT_MESSAGE)
                        .dateTime(msg.getTimestamp())
                        .data(chatMessageMapper.toChatMessageResponse(msg,
                                null, null))
                        .build())
                .toList();


        List<ChatAndTransactionUnifiedDTO> transactionItems = senderTransactions
                .stream().map(
                        transaction -> ChatAndTransactionUnifiedDTO
                                .builder()
                                .type(ChatDtoDataType.TRANSACTION_MESSAGE)
                                .dateTime(transaction.getChatDateTime())
                                .data(transaction)
                                .build()).toList();

        // 3. Merge + Sort
        List<ChatAndTransactionUnifiedDTO> unifiedList = new ArrayList<>();
        unifiedList.addAll(chatItems);
        unifiedList.addAll(transactionItems);

        unifiedList.sort(Comparator
                .comparing(ChatAndTransactionUnifiedDTO::getDateTime));

        log.info("Unified response prepared. Total items: {}",
                unifiedList.size());

        return unifiedList;
    }

    /**
     * Retrieves user details based on receiver ID (phone number).
     *
     * @param receiverId
     * @return
     */
    @Override
    public User getReceiverUserDetails(final String receiverId) {
        try {
            log.info("Finding user by phoneNumber : {}",
                    "****" + receiverId.substring(
                            SUBSTRING_START_INDEX, SUBSTRING_END_INDEX));
            return validations.getUserByPhoneNumber(receiverId);
        } catch (UserNotFoundException e) {
            throw new WebSocketUserNotFoundException(
                    "This user in not registered on "
                            + "sundukpay!");
        }
    }

    /**
     * Retrieves phone number by user UUID with caching.
     *
     * @param uuid the user UUID
     * @return the phone number associated with the UUID
     */
    @Cacheable(value = "userPhoneCache", key = "#uuid")
    private String getPhoneNumberbyUuid(final String uuid) {
        User user = validations.getUserInfo(uuid);
        return user.getPhoneNumber();
    }

}


