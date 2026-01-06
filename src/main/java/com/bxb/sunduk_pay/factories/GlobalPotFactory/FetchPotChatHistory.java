package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.GroupChatUnifiedDTO;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.util.ChatDtoDataType;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FetchPotChatHistory implements GlobalPotOperation {
    /**
     * Redis template for fetching group chat message responses.
     */
    private final RedisTemplate<String,
            GroupChatMessageResponse> messageResponseRedisTemplate;

    /**
     * Redis template for fetching transaction responses.
     */
    private final RedisTemplate<String,
            TransactionResponse> transactionRedisTemplate;

    /**
     * Repository for transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Mapper for transactions.
     */
    private final TransactionMapper transactionMapper;

    /**
     * Mapper for GlobalPot related conversions.
     */
    private final GlobalPotMapper globalPotMapper;

    /**
     * Validations for GlobalPot operations.
     */
    private final GlobalPotValidations globalPotValidations;

    /**
     * General validations utility.
     */
    private final Validations validations;

    /**
     * Utility for generating keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_GROUP_CHAT_HISTORY;
    }

    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {
        log.info("Fetching chat history for pot {}",
                request.getGlobalPotId());
        String groupChatKey = generateKeyUtil
                .getGroupChatKey(request.getGlobalPotId());
        log.info("Generated Redis key for group chat: {}",
                groupChatKey);

        String groupTransactionKey = generateKeyUtil
                .getGroupTransactionKey(request.getGlobalPotId());
        log.info("Generated Redis key for group transactions: {}",
                groupTransactionKey);


        validations.getUserInfo(request.getUuid());
        globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("Validated user with UUID: {}",
                request.getUuid());

        List<GroupChatMessageResponse> groupChatMessages =
                messageResponseRedisTemplate.opsForList()
                        .range(groupChatKey, 0, -1);
        log.info("Fetched {} chat messages from Redis for pot {}",
                groupChatMessages != null ? groupChatMessages.size() : 0,
                request.getGlobalPotId());

        if (groupChatMessages == null || groupChatMessages.isEmpty()) {
            List<GroupChatMessage> groupChatMessagesFromDB =
                    globalPotValidations.getGroupChatMessagesFromDB(
                            request.getGlobalPotId());
            log.info("Fetched {} chat messages from DB for pot {}",
                    groupChatMessagesFromDB.size(),
                    request.getGlobalPotId());

            groupChatMessages = globalPotMapper
                    .toGroupChatMessageResponseList(groupChatMessagesFromDB);

            if (!groupChatMessages.isEmpty()) {
                messageResponseRedisTemplate.opsForList().rightPushAll(groupChatKey,
                        globalPotMapper.toGroupChatMessageResponseList(
                                groupChatMessagesFromDB));
                messageResponseRedisTemplate.expire(groupChatKey, Duration.ofMinutes(5));
                log.info("Cached {} chat messages to Redis for pot {}",
                        groupChatMessages.size(),
                        request.getGlobalPotId());
            }
        } else {
            log.info("Fetched {} chat messages from Redis for pot {}",
                    groupChatMessages.size(),
                    request.getGlobalPotId());
        }

        List<TransactionResponse> groupTransactions = transactionRedisTemplate
                .opsForList().range(groupTransactionKey, 0, -1);
        log.info("Fetched {} transactions from Redis for pot {}",
                groupTransactions != null ? groupTransactions.size() : 0,
                request.getGlobalPotId());

        if (groupTransactions == null || groupTransactions.isEmpty()) {
            log.info("Fetching transactions from DB for pot {}",
                    request.getGlobalPotId());
            List<Transaction> dbTransactions = transactionRepository
                    .findByTransactionLevelAndToGlobalPotIdAndIsMasterFalse(
                            TransactionLevel.CONTRIBUTOR,
                            request.getGlobalPotId());
            log.info("Fetched {} transactions from DB for pot {}",
                    dbTransactions.size(),
                    request.getGlobalPotId());

            groupTransactions = transactionMapper
                    .toTransactionsResponse(dbTransactions);

            if (!groupTransactions.isEmpty()) {
                transactionRedisTemplate.opsForList().rightPushAll(
                        groupTransactionKey, groupTransactions);
                transactionRedisTemplate.expire(groupTransactionKey, Duration.ofMinutes(5));
                log.info("Cached {} transactions to Redis for pot {}",
                        groupTransactions.size(),
                        request.getGlobalPotId());
            }
        } else {
            log.info("Fetched {} transactions from Redis for pot {}",
                    groupTransactions.size(),
                    request.getGlobalPotId());
        }

        List<GroupChatUnifiedDTO> chatUnifiedDTOS = groupChatMessages.stream()
                .map(message -> GroupChatUnifiedDTO
                        .builder()
                        .dataType(ChatDtoDataType.CHAT_MESSAGE)
                        .timestamp(message.getTimestamp())
                        .data(message).build()).toList();
        log.info("Mapped chat messages to unified DTOs for pot {}",
                request.getGlobalPotId());

        List<GroupChatUnifiedDTO> transactionUnifiedDTOS = groupTransactions
                .stream().map(txn -> GroupChatUnifiedDTO
                        .builder()
                        .dataType(ChatDtoDataType.TRANSACTION_MESSAGE)
                        .timestamp(txn.getChatDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toInstant().toString())
                        .data(txn).build()).toList();
        log.info("Mapped transactions to unified DTOs for pot {}",
                request.getGlobalPotId());

        List<GroupChatUnifiedDTO> combinedList = new ArrayList<>();
        combinedList.addAll(chatUnifiedDTOS);
        combinedList.addAll(transactionUnifiedDTOS);

        combinedList.sort(Comparator
                .comparing(GroupChatUnifiedDTO::getTimestamp));
        log.info("Combined chat and transaction messages for pot {}",
                request.getGlobalPotId());

        return GlobalPotResponse.builder()
                .message("Fetched chat history successfully")
                .groupChatHistory(combinedList).build();

    }
}
