package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.ChatProcessingException;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GroupChatMessageRepository;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.GroupChatMessageService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the GroupChatMessageService interface.
 * Handles processing of group chat messages.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class GroupChatMessageServiceImpl implements GroupChatMessageService {

    /**
     * Repository for group chat messages.
     */
    private final GroupChatMessageRepository groupChatMessageRepository;

    /**
     * Validations utility.
     */
    private final Validations validations;

    /**
     * Validations specific to GlobalPot.
     */
    private final GlobalPotValidations globalPotValidations;

    /**
     * Mapper for GlobalPot related conversions.
     */
    private final GlobalPotMapper globalPotMapper;

    /**
     * Redis template for caching group chat message responses.
     */
    private final RedisTemplate<String,
            GroupChatMessageResponse> messageResponseRedisTemplate;

    /**
     * Redis template for caching transaction responses.
     */
    private final RedisTemplate<String,
            TransactionResponse> transactionResponseRedisTemplate;

    /**
     * Utility for generating keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * Processes a group chat message event.
     *
     * @param event the group chat event
     * @return the response after processing the group chat message
     */
    @Override
    public GroupChatMessageResponse processGroupChatMessage(
            final GroupChatEvent event) {
        try {
            log.info(
                    "Processing group chat message in service for pot {}",
                    event.getGlobalPotId());

            User user = validations.getUserInfo(event.getSenderId());
            log.info("Validated sender user: {}", user.getUuid());

            GlobalPot globalPot = globalPotValidations
                    .getGlobalPot(event.getGlobalPotId());
            log.info("Validated GlobalPot: {}", globalPot.getGlobalPotId());


            GroupChatMessage message = GroupChatMessage.builder()
                    .sender(user)
                    .globalPot(globalPot)
                    .content(event.getContent())
                    .timestamp(Instant.now())
                    .build();

            // Save the message in Redis cache
            saveMessageInRedisCache(message);
            log.info(
                    "Saved group chat message in Redis cache for pot {}",
                    event.getGlobalPotId());

            // Persist the message in the database
            groupChatMessageRepository.save(message);
            log.info(
                    "Persisted group chat message to DB for pot {}",
                    event.getGlobalPotId()

            );
            return globalPotMapper.toGroupChatMessageResponse(message);
        } catch (Exception e){
            log.error("Error processing group chat message: {}",
                    e.getMessage());
            throw new ChatProcessingException(
                    "Error processing group chat message: "
                            + e.getMessage());
        }
    }


    /**
     * Saves the group chat message in Redis cache.
     *
     * @param message the group chat message to be cached
     */
    private void saveMessageInRedisCache(final GroupChatMessage message) {

        try {
            // First, check if the group chat messages for this pot
            // are already cached in Redis
            String groupChatKey = generateKeyUtil.getGroupChatKey(
                    message.getGlobalPot().getGlobalPotId());

            // If not cached, load existing messages from DB and cache them
            if (!messageResponseRedisTemplate.hasKey(groupChatKey)) {
                log.info(
                        "Group chat messages for key {} not found in Redis cache. Loading from DB...",
                        groupChatKey);
                List<GroupChatMessage> groupMessagesFromDb =
                        groupChatMessageRepository.findByGlobalPotGlobalPotId(
                                message.getGlobalPot().getGlobalPotId());

                // Cache the messages in Redis
                if (!groupMessagesFromDb.isEmpty()) {
                    log.info(
                            "Found {} group chat messages in DB for pot {}. Caching in Redis...",
                            groupMessagesFromDb.size(),
                            message.getGlobalPot().getGlobalPotId());
                    // Map DB messages to response DTOs
                    List<GroupChatMessageResponse> groupMessagesResponse =
                            groupMessagesFromDb.stream()
                                    .map(globalPotMapper::toGroupChatMessageResponse)
                                    .toList();

                    // Store the list of messages in Redis
                    messageResponseRedisTemplate.opsForList().rightPushAll(
                            groupChatKey, groupMessagesResponse);
                    log.info(
                            "Loaded {} group chat messages from DB to Redis cache for key {}",
                            groupMessagesResponse.size(), groupChatKey);
                }
            }
            // Now, add the new message to Redis cache
            GroupChatMessageResponse groupChatMessageResponse = globalPotMapper
                    .toGroupChatMessageResponse(message);

            log.info(
                    "Adding new group chat message to Redis cache for key {}",
                    groupChatKey);

            // Push the new message to the end of the list
            messageResponseRedisTemplate.opsForList().rightPush(groupChatKey,
                    groupChatMessageResponse);
            messageResponseRedisTemplate.expire(groupChatKey, Duration.ofHours(1));
        } catch(Exception e){
            log.error("Error saving group chat message in Redis cache: {}",
                    e.getMessage());
            throw new RedisOperationException(
                    "Error saving group chat message in Redis cache: "
                            + e.getMessage());
        }
    }
}
