package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;
import com.bxb.sunduk_pay.util.*;
import com.bxb.sunduk_pay.response.GroupChatUnifiedDTO;
import com.bxb.sunduk_pay.service.AnonymousUserService;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * {@code AddContributerService} handles the business logic
 * for adding a contributor to a Global Pot.
 *
 * <p>This service performs the following operations:</p>
 * <ul>
 *     <li>Fetches user and GlobalPot information</li>
 *     <li>Validates sufficient balance in wallets</li>
 *     <li>Deducts amount from Master/Main/Sub wallet</li>
 *     <li>Credits amount to GlobalWallet</li>
 *     <li>Creates and stores transaction records</li>
 *     <li>Updates contributed balance of GlobalPot</li>
 * </ul>
 *
 * <p>This service is transactional, ensuring atomicity
 * of all wallet and transaction operations.</p>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AddContributorService implements GlobalPotOperation {

    /** Constant for five minutes duration. */
    private static final int FIVE = 5;

    /** Repository for contributor data access. **/
    private final ContributerRepository contributerRepository;
    /** Validations specific to GlobalPot operations. **/
    private final GlobalPotValidations globalPotValidations;
    /** Mapper for GlobalPot related entities. **/
    private final GlobalPotMapper globalPotMapper;
    /** Validations utility for input validation and data retrieval. **/
    private final Validations validations;
    /** Repository for MainWallet data access. **/
    private final MainWalletRepository mainWalletRepository;
    /** Repository for MasterWallet data access. **/
    private final MasterWalletRepository masterWalletRepository;
    /** Repository for GlobalWallet data access. **/
    private final GlobalWalletRepository globalWalletRepository;
    /** Repository for transaction data access. **/
    private final TransactionRepository transactionRepository;
    /** Repository for SubWallet data access. **/
    private final SubWalletRepository subWalletRepository;
    /** Repository for GlobalPot data access. **/
    private final GlobalPotRepository globalPotRepository;
    /** Redis template for caching and messaging. **/
    private final RedisTemplate<String, TransactionResponse> redisTemplate;
    /** Utility for generating unique keys. **/
    private final GenerateKeyUtil generateKeyUtil;
    /** Mapper for transaction entities to response DTOs. **/
    private final TransactionMapper transactionMapper;
    /** Service for handling anonymous user identities. **/
    private final AnonymousUserService anonymousUserService;
    /** Messaging template for WebSocket communication. **/
    private final SimpMessagingTemplate messagingTemplate;
/** Repository for GlobalPotMembers data access. **/
    private final GlobalPotMembersRepository membersRepository;
    /** Repository for follower data access. **/
private final FollowerRepository followerRepository;

/** Repository for GlobalPotTransaction data access. **/
private final GlobalPotTransactionRepository globalPotTransactionRepository;
    /**
     * Returns the {@link GlobalPotRequestType} handled by this service.
     *
     * @return {@link GlobalPotRequestType#ADD_CONTRIBUTOR}
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.ADD_CONTRIBUTOR;
    }

    /**
     * Adds a contributor to a Global Pot by transferring the specified amount
     * from the user's wallet to the Global Pot wallet.
     *
     * <p>The method performs validation, wallet balance deduction,
     * transaction creation, and persistence of all related entities.</p>
     *
     * @param request {@link GlobalPotRequest} containing contributor details,
     *                wallet information, and contribution amount
     * @return {@link GlobalPotResponse} with success message
     * @throws IOException                  if any I/O related issue occurs
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        log.info(
       "AddContributor started | userId={} globalPotId={} amount={}",
                request.getUserContributorId(),
                request.getGlobalPotId(),
                request.getAmountContributed());

        User user = validations.getUserInfo(request.getUserContributorId());
        log.info(
      "User fetched successfully | userId={}", user.getUuid());

        MainWallet mainWallet =
                validations.getMainWalletInfo(request.getUserContributorId());

        MasterWallet masterWallet = validations.getMasterWalletInfo(
                request.getUserContributorId());

        SubWallet subWallet =
                validations.findSubWalletIfExists(
                        mainWallet.getMainWalletId(),
                        request.getSourceWalletId()
                );


        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("GlobalPot fetched | globalPotId={}",
                globalPot.getGlobalPotId());

        GlobalWallet globalWallet =
                globalWalletRepository.findById(
                        globalPot.getGlobalWallet().getGlobalWalletId()
                ).orElseThrow(() -> new WalletNotFoundException(
                        "GlobalWallet not found"));
        createFollowerRecord(user,globalPot);
        createMemberRecord(user,globalPot);

        String groupTransactionRedisKey = generateKeyUtil
                .getGroupTransactionKey(request.getGlobalPotId());
        log.info("Generated Redis key for group transactions: {}",
                groupTransactionRedisKey);

        Boolean isAnonymous = request.getIsAnonymous() != null
                ? request.getIsAnonymous() : null;

        AnonymousIdentityDTO identity = null;

        if (Boolean.TRUE.equals(isAnonymous)) {
            log.info("Anonymous boolean flag found as true,"
                    + " masking transaction as anonymous.");
            identity = anonymousUserService
                    .getOrCreateAnonymousColor(user, globalPot);
        }


        log.info("MasterWallet balance before deduction={}",
                masterWallet.getBalance());

        validations.validateBalance(
                masterWallet.getBalance(),
                request.getAmountContributed()
        );

        masterWallet.setBalance(
                masterWallet.getBalance() - request.getAmountContributed()
        );

        log.info("MasterWallet balance after deduction={}",
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();

        //user master wallet transaction
        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .isMaster(true)
                .isInvestment(false)
                .transactionLevel(TransactionLevel.CONTRIBUTOR)
                .amount(request.getAmountContributed())
                .transactionType(TransactionType.DEBIT)
                .status("SUCCESS")
                .description("Deducted from master wallet")
                .dateTime(LocalDateTime.now())
                .user(user)
                .fromWallet("Master Wallet")
                .fromWalletId(masterWallet.getMasterWalletId())
                .toGlobalPotId(globalPot.getGlobalPotId())
                .isAnonymous(isAnonymous)
                .paymentTag(request.getPaymentTag())
                .anonymousId(
                        identity != null ? identity.getAnonymousId() : null)
                .anonymousColor(
                        identity != null ? identity.getAnonymousColor() : null)
                .build();

        transactions.add(masterWalletTxn);

        if (subWallet != null) {

            log.info("SubWallet found | subWalletId={}",
                    subWallet.getSubWalletId());

            log.info("SubWallet balance before deduction={}",
                    subWallet.getBalance());

            validations.validateBalance(
                    subWallet.getBalance(),
                    request.getAmountContributed()
            );

            subWallet.setBalance(
                    subWallet.getBalance() - request.getAmountContributed()
            );

            log.info("SubWallet balance after deduction={}",
                    subWallet.getBalance());

            //user sub wallet transaction
            Transaction subWalletTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .isMaster(false)
                    .isInvestment(false)
                    .transactionLevel(TransactionLevel.CONTRIBUTOR)
                    .amount(request.getAmountContributed())
                    .transactionType(TransactionType.DEBIT)
                    .status("SUCCESS")
                    .description("Deducted from sub wallet")
                    .paymentTag(request.getPaymentTag())
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet(subWallet.getSubWalletName())
                    .fromWalletId(request.getSourceWalletId())
                    .toGlobalPotId(globalPot.getGlobalPotId())
                    .isAnonymous(isAnonymous)
                    .anonymousId(
                            identity != null
                                    ? identity.getAnonymousId() : null)
                    .anonymousColor(
                            identity != null
                                    ? identity.getAnonymousColor() : null)
                    .build();

            GlobalPotTransaction globalPotTransaction =
                    createGlobalPotTransaction(globalPot, globalWallet,
                    request.getAmountContributed(), user, subWalletTxn
                            .getTransactionId());
            // Create TransactionResponse for Redis and WebSocket forwarding
            TransactionResponse transactionResponse = transactionMapper
                    .toTransactionResponse(subWalletTxn);
            // Push to Redis list for group transactions
            redisTemplate.opsForList().rightPush(groupTransactionRedisKey,
                    transactionResponse);
            transactions.add(subWalletTxn);
            globalPotTransactionRepository.save(globalPotTransaction);
            subWalletRepository.save(subWallet);

            // Forward the transaction message to WebSocket clients
            forwardMessageToWebSocket(globalPot.getGlobalPotId(),
                    transactionResponse);

        } else {

    log.info("No SubWallet found, using MainWallet | mainWalletId={}",
                    mainWallet.getMainWalletId());

            log.info("MainWallet balance before deduction={}",
                    mainWallet.getBalance());

            validations.validateBalance(
                    mainWallet.getBalance(),
                    request.getAmountContributed()
            );

            mainWallet.setBalance(
                    mainWallet.getBalance() - request.getAmountContributed()
            );

            log.info("MainWallet balance after deduction={}",
                    mainWallet.getBalance());

            //user main wallet transaction
            Transaction mainWalletTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .isMaster(false)
                    .isInvestment(false)
                    .transactionLevel(TransactionLevel.CONTRIBUTOR)
                    .amount(request.getAmountContributed())
                    .transactionType(TransactionType.DEBIT)
                    .status("SUCCESS")
                    .description("Deducted from main wallet")
                    .paymentTag(request.getPaymentTag())
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet("Main Wallet")
                    .fromWalletId(request.getSourceWalletId())
                    .toGlobalPotId(globalPot.getGlobalPotId())
                    .isAnonymous(isAnonymous)
                    .anonymousId(
                            identity != null
                                    ? identity.getAnonymousId() : null)
                    .anonymousColor(
                            identity != null
                                    ? identity.getAnonymousColor() : null)
                    .build();

            GlobalPotTransaction globalPotTransaction =
                    createGlobalPotTransaction(globalPot, globalWallet,
                            request.getAmountContributed(), user, mainWalletTxn
                                    .getTransactionId());

            // Create TransactionResponse for Redis and WebSocket forwarding
            TransactionResponse transactionResponse = transactionMapper
                    .toTransactionResponse(mainWalletTxn);
            // Push to Redis list for group transactions
            redisTemplate.opsForList().rightPush(groupTransactionRedisKey,
                    transactionResponse);
            transactions.add(mainWalletTxn);
            globalPotTransactionRepository.save(globalPotTransaction);
            mainWalletRepository.save(mainWallet);
            // Forward the transaction message to WebSocket clients
            forwardMessageToWebSocket(globalPot.getGlobalPotId(),
                    transactionResponse);
        }

        log.info("GlobalWallet balance before credit={}",
                globalWallet.getBalance());

        globalWallet.setBalance(
                globalWallet.getBalance() + request.getAmountContributed()
        );

        log.info("GlobalWallet balance after credit={}",
                globalWallet.getBalance());

        log.info("GlobalPot currentBalance before update={}",
                globalPot.getCurrentBalance());
        globalPot.setCurrentBalance(globalPot.getCurrentBalance()
                + request.getAmountContributed());
        log.info("GlobalPot currentBalance after update={}",
                globalPot.getCurrentBalance());

        log.info("GlobalPot contributedBalance before update={}",
                globalPot.getContributedBalance());

        globalPot.setContributedBalance(
                globalPot.getContributedBalance()
                        + request.getAmountContributed()
        );

        log.info("GlobalPot contributedBalance after update={}",
                globalPot.getContributedBalance());

        Contributor contributor =
                globalPotMapper.toContributerEntity(request, globalPot);

        redisTemplate.expire(groupTransactionRedisKey,
                Duration.ofMinutes(FIVE));
        masterWalletRepository.save(masterWallet);
        transactionRepository.saveAll(transactions);
        globalWalletRepository.save(globalWallet);
        globalPotRepository.save(globalPot);
        contributerRepository.save(contributor);
        log.info(
"Contributor added successfully | userId={} globalPotId={} amount={}",
                request.getUserContributorId(),
                request.getGlobalPotId(),
                request.getAmountContributed());

        return GlobalPotResponse.builder()
                .message("Contributor added successfully")
                .build();
    }

    /**
     * Forwards the transaction message to WebSocket clients
     * subscribed to the specified Global Pot.
     *
     * @param globalPotId the ID of the Global Pot
     * @param response    the transaction response to forward
     */
    private void forwardMessageToWebSocket(
            final String globalPotId,
            final TransactionResponse response) {

        GroupChatUnifiedDTO transactionWebsocketDTO = getTransactionDTO(
                response);
        // Implementation for forwarding message to WebSocket clients
        messagingTemplate.convertAndSend(
                "/topic/group/" + globalPotId,
                transactionWebsocketDTO);
        log.info(
                "Forwarded global pot contribution transaction "
                        + "to WebSocket for group {}",
                globalPotId);
        log.info(
        "=========== Finished processing group chat message ===========");
    }

    /**
     * Converts GroupChatMessageResponse to GroupChatUnifiedDTO.
     * @param response the group chat message response
     * @return the unified DTO representation
     */
    private GroupChatUnifiedDTO getTransactionDTO(
            final TransactionResponse response) {
        return GroupChatUnifiedDTO.builder()
                .dataType(ChatDtoDataType.TRANSACTION_MESSAGE)
                .timestamp(response.getChatDateTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toString())
                .data(response)
                .build();
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
        Optional<GlobalPotMembers> dbMember
                = membersRepository.findByUserAndGlobalPot(user, globalPot);
        if (dbMember.isPresent() && Boolean.TRUE
                .equals(dbMember.get().getIsContributor())) {
            log.info(
     "User {} is already a contributor of pot {}. No action taken.",
                    user.getUuid(), globalPot.getGlobalPotId());
            return;
        }
        else if (dbMember.isPresent() && Boolean.FALSE
                .equals(dbMember.get().getIsContributor())) {
            log.info(
"User {} is already a member of pot {}. No action taken.",
                    user.getUuid(), globalPot.getGlobalPotId());
            dbMember.get().setIsContributor(true);
            membersRepository.saveAndFlush(dbMember.get());
            return;
        } else {
            log.info(
                    "Creating new member record for user {} in pot {}",
                    user.getUuid(), globalPot.getGlobalPotId());
            GlobalPotMembers member = GlobalPotMembers.builder()
                    .user(user)
                    .globalPot(globalPot)
                    .isBlocked(false)
                    .userRoles(UserRoles.NORMAL_USER)
                    .createdAt(LocalDateTime.now())
                    .isContributor(true)
                    .build();
            membersRepository.save(member);
        }
    }

    private GlobalPotTransaction createGlobalPotTransaction(
            final GlobalPot globalPot,
            final GlobalWallet globalWallet,
            final Double amount,
            final User admin,
            final String sourceTransactionId
    ){
        return GlobalPotTransaction.builder()
                .globalPot(globalPot)
                .globalWallet(globalWallet)
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .description("Amount credited -" + amount
                        + " to Global Pot")
                .user(admin)
                .sourceUserTransactionId(sourceTransactionId)
                .dateTime(LocalDateTime.now()).build();
    }
}
