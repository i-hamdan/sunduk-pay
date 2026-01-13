package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.util.UserRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of Global Pot validations.
 * Provides methods to validate and retrieve Global Pot data.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class GlobalPotValidationsImpl implements GlobalPotValidations {

    /**
     * Repository for accessing Global Pot data.
     */
    private final GlobalPotRepository globalPotRepository;

    /**
     * Repository for accessing Group Chat Message data.
     */
    private final GroupChatMessageRepository groupChatMessageRepository;

    /**
     * Repository for accessing Contributor data.
     */
    private final ContributerRepository contributerRepository;


    private final GlobalPotMembersRepository globalPotMembersRepository;



    /**
     * Validates the existence of a Global Pot by its ID.
     *
     * @param globalPotId the ID of the Global Pot to validate
     * @return the validated Global Pot
     * @throws GlobalPotNotFoundException if the Global Pot is not found
     */
    @Override
    public GlobalPot getGlobalPot(final String globalPotId) {
        return globalPotRepository.findById(globalPotId).orElseThrow(
                () -> new GlobalPotNotFoundException(
                        "Global Pot not found with ID: "
                                + globalPotId));

    }

    /**
     * Retrieves the count of contributors for a given Global Pot.
     *
     * @param globalPotId the ID of the Global Pot
     * @return the number of contributors
     */
    @Override
    public int getContributorsCount(final String globalPotId) {
        return globalPotRepository
                .getContributorsCountGlobalByPotId(globalPotId);
    }

    /**
     * Retrieves the count of followers for a given Global Pot.
     *
     * @param globalPotId the ID of the Global Pot
     * @return the number of followers
     */
    @Override
    public int getFollowersCount(final String globalPotId) {
        return globalPotRepository
                .getFollowersCountGlobalByPotId(globalPotId);
    }

    @Override
    public void validateAmountContributed(Double amountContributed) {

        if (amountContributed == null || amountContributed <= 0) {
            throw new InsufficientBalanceException(
                    "Amount contributed is null or negative"
            );
        }

    }

    @Override
    public void validateAdmin(User admin) {
        if (admin == null) {
            throw new UserNotFoundException(
                    "Admin user not found for the Global Pot"
            );
        }

            if (admin.getUserRole()!= UserRoles.GLOBALPOT_ADMIN) {
            throw new UserNotFoundException(
                    "User is not authorized as Global Pot Admin"
            );
        }


    }

    @Override
    public List<GroupChatMessage> getGroupChatMessagesFromDB(String globalPotId) {
        return groupChatMessageRepository.findByGlobalPotGlobalPotId(globalPotId);
    }



    @Override
    public void validateUserHasContributed(String globalPotId, String userUuid) {
        if (!contributerRepository
                .existsByGlobalPotGlobalPotIdAndUserContributorUuid(
                        globalPotId, userUuid)) {

            throw new ResourceNotFoundException(
                    "Only contributors can be blocked"
            );
        }
    }


    @Override
    public void ensureUserIsMember(User userId, GlobalPot globalPotId) {

        boolean isAlreadyMember =
                globalPotMembersRepository
                        .existsByUserAndGlobalPot(userId,
                                globalPotId);

        if (isAlreadyMember) {

            return;
        }
        GlobalPotMembers member = GlobalPotMembers.builder()
                .user(userId)
                .globalPot(globalPotId)
                .userRoles(userId.getUserRole())
                .isBlocked(false)
                .build();

        globalPotMembersRepository.save(member);
    }

    @Override
    public void ensureUserIsMemberByAdmin(User userId,
                                          GlobalPot globalPotId) {

        boolean isAlreadyMember =
                globalPotMembersRepository
                        .existsByUserAndGlobalPot(
                                userId,
                                globalPotId);

        if (isAlreadyMember) {
            throw new UserAlreadyExist(
                    "User with uuid [" + userId.getFullName() +
                            "] is already a member of Global Pot [" +
                            globalPotId.getCaseTitle() + "]"
            );
        }

        GlobalPotMembers member = GlobalPotMembers.builder()
                .user(userId)
                .globalPot(globalPotId)
                .userRoles(userId.getUserRole())
                .build();

        globalPotMembersRepository.save(member);
    }

    @Override
    public GlobalPotMembers getMemberByUuidAndGlobalPotId(
            final User userUuid,
            final GlobalPot globalPotId) {
        return globalPotMembersRepository.findByUserAndGlobalPot(
                userUuid,globalPotId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Member not found with UUID: "
                                + userUuid.getUuid() + " in Global Pot ID: "
                                + globalPotId.getGlobalPotId()));
    }


}



