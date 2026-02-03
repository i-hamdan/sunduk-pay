package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.ChatProcessingException;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.FollowerRepository;
import com.bxb.sunduk_pay.repository.GlobalPotInteractionRepository;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.repository.GroupChatMessageRepository;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.service.AnonymousUserService;
import com.bxb.sunduk_pay.service.GroupChatMessageService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the GroupChatMessageService interface.
 * Handles processing of group chat messages.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class GroupChatMessageServiceImpl implements GroupChatMessageService {

    /**
     * Duration for which the group chat messages.
     * are cached in Redis (in minutes).
     */
    private static final int DURATION_IN_MINUTES = 5;
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
     * Utility for generating keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * Repository for follower relationships.
     */
    private final FollowerRepository followerRepository;

    /**
     * Service for handling anonymous user features.
     */
    private final AnonymousUserService anonymousUserService;

    /**
     * Repository for global pot members.
     */
    private final GlobalPotMembersRepository membersRepository;

    private final GlobalPotInteractionRepository globalPotInteractionRepository;

    /**
     * Processes a group chat message event.
     *
     * @param event the group chat event
     * @return the response after processing the group chat message
     */
    @Override
    @Transactional
    public GroupChatMessageResponse processGroupChatMessage(
            final GroupChatEvent event) {
        try {
            log.info("Processing group chat message for pot {}",
                    event.getGlobalPotId());

            User user = validations.getUserInfo(event.getSenderId());
            log.info("Fetched user info for sender ID {}: {}",
                    event.getSenderId(), user.getUuid());
            GlobalPot globalPot =
                    globalPotValidations.getGlobalPot(event.getGlobalPotId());
            log.info("Fetched global pot info for pot ID {}: {}",
                    event.getGlobalPotId(), globalPot.getGlobalPotId());

            createFollowerRecord(user, globalPot);
            createMemberRecord(user, globalPot);

            boolean isAnonymous = event.isAnonymous();
            log.info("Is the message anonymous? {}", isAnonymous);
            AnonymousIdentityDTO anonymousUser = null;
            String anonymousColor = null;
            String anonymousId = null;

            if (isAnonymous) {
                log.info(
           "Fetching or creating anonymous identity for user {} in pot {}",
                        user.getUuid(), globalPot.getGlobalPotId());
                anonymousUser = anonymousUserService
                        .getOrCreateAnonymousColor(user, globalPot);
                if (anonymousUser != null) {
                    log.info(
                            "Obtained anonymous identity: ID={}, Color={}",
                            anonymousUser.getAnonymousId(),
                            anonymousUser.getAnonymousColor());
                    anonymousColor = anonymousUser.getAnonymousColor();
                    anonymousId = anonymousUser.getAnonymousId();
                }
            }

            log.info("Creating group chat message entity...");
            GroupChatMessage message = GroupChatMessage.builder()
                    .sender(user)
                    .globalPot(globalPot)
                    .content(event.getContent())
                    .timestamp(Instant.now())
                    .isAnonymous(isAnonymous)
                    .anonymousId(anonymousId)
                    .anonymousColor(anonymousColor)
                    .build();

            // Persist FIRST
            groupChatMessageRepository.save(message);
            log.info(
                    "Saved group chat message with ID {} to database.",
                    message.getMessageId());

            // Map AFTER persistence
            GroupChatMessageResponse response =
                    globalPotMapper.toGroupChatMessageResponse(message);
            log.info("Mapped group chat message to response DTO.");

            // Cache AFTER DB
            saveMessageInRedisCache(message);
            log.info(
                    "Group chat message processing completed for pot {}",
                    event.getGlobalPotId());

            GlobalPotInteraction globalPotInteraction =
                    globalPotInteractionRepository.findByUuidAndGlobalPot(
                                    user, globalPot)
                            .orElseGet(()-> getInteraction(user,globalPot));

            globalPotInteraction.setMessageCount(
                    globalPotInteraction.getMessageCount() + 1);

            globalPotInteraction.setLastMessageAt(LocalDateTime.now());

            globalPotInteraction.setLastInteractedAt(LocalDateTime.now());

            globalPotInteractionRepository.save(globalPotInteraction);

            return response;

        } catch (Exception e) {
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
    protected void saveMessageInRedisCache(final GroupChatMessage message) {

        try {
            // First, check if the group chat messages for this pot
            // are already cached in Redis
            String groupChatKey = generateKeyUtil.getGroupChatKey(
                    message.getGlobalPot().getGlobalPotId());
            log.info(
                    "Generated Redis key for group chat: {}",
                    groupChatKey);

            // If not cached, load existing messages from DB and cache them
            if (!messageResponseRedisTemplate.hasKey(groupChatKey)) {
                log.info("Group chat messages for key"
                        + groupChatKey + "not found in Redis cache. Loading from DB..."
                );
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
                    log.info("Loaded"
                            + groupMessagesResponse.size()
                            + " group chat messages from DB "
                            + " to Redis cache for key "
                            + groupChatKey);
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
            messageResponseRedisTemplate.expire(
                    groupChatKey, Duration.ofMinutes(DURATION_IN_MINUTES));
        } catch (Exception e) {
            log.error(
                    "Error saving group chat message in Redis cache: {}",
                    e.getMessage());
            throw new RedisOperationException(
                    "Error saving group chat message in Redis cache: "
                            + e.getMessage());
        }
    }

    /**
     * Creates a new follower record for the user in the specified global pot.
     *
     * @param user      the user who is following
     * @param globalPot the global pot being followed
     */
    private void createFollowerRecord(
            final User user, final GlobalPot globalPot) {

        if (followerRepository.existsByFollowerUserAndGlobalPot(
                user, globalPot)) {
            log.info(
     "User {} is already a follower of pot {}. No action taken.",
                    user.getUuid(), globalPot.getGlobalPotId());
            return;
        } else {
            log.info(
         "Creating new follower record for user {} in pot {}",
                    user.getUuid(), globalPot.getGlobalPotId());

            Follower follower = Follower.builder()
                    .followerUser(user)
                    .globalPot(globalPot)
                    .createdAt(LocalDateTime.now()).build();
            followerRepository.save(follower);
        }
    }

    /**
     * Creates a new member record for the user in the specified global pot.
     *
     * @param user      the user to be added as a member
     * @param globalPot the global pot to which the user is being added
     */
    private void createMemberRecord(
            final User user, final GlobalPot globalPot) {

        log.info("Checking if user {} is already a member of pot {}",
                user.getUuid(), globalPot.getGlobalPotId());
        if (membersRepository.existsByUserAndGlobalPot(user, globalPot)) {
            log.info(
      "User {} is already a member of pot {}. No action taken.",
                    user.getUuid(), globalPot.getGlobalPotId());
            return;
        } else {
            log.info(
               "Creating new member record for user {} in pot {}",
                    user.getUuid(), globalPot.getGlobalPotId());
            GlobalPotMembers member = GlobalPotMembers.builder()
                    .user(user)
                    .globalPot(globalPot)
                    .isBlocked(false)
                    .isContributor(false)
                    .userRoles(UserRoles.NORMAL_USER)
                    .createdAt(LocalDateTime.now())
                    .build();
            membersRepository.save(member);
        }
    }

    /**
     * Creates a new GlobalPotInteraction record for the user and global pot.
     *
     * @param user      the user involved in the interaction
     * @param globalPot the global pot involved in the interaction
     * @return the created GlobalPotInteraction object
     */
    private GlobalPotInteraction getInteraction(
            final User user,
            final GlobalPot globalPot){

        return GlobalPotInteraction.builder()
                .uuid(user)
                .globalPot(globalPot)
                .caseCategory(globalPot.getCaseCategory())
                .visitCount(0)
                .messageCount(0)
                .lastInteractedAt(LocalDateTime.now())
                .contributionCount(0)
                .totalContributedAmount(0d)
                .totalTimeSpentInSeconds(0)
                .build();
    }
}
