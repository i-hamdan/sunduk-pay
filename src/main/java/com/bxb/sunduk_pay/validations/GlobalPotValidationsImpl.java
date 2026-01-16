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
 * Implementation of Global Pot validation operations.
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

    /**
     * Repository for accessing Global Pot Members data.
     */
    private final GlobalPotMembersRepository globalPotMembersRepository;

    /**
     * Repository for accessing Global Pot Document data.
     */
    private final GlobalPotDocumentRepository globalPotDocumentRepository;




    /**
     * Validates the existence of a Global Pot by its ID.
     *
     * @param globalPotId the ID of the Global Pot to validate
     * @return the validated Global Pot
     * @throws GlobalPotNotFoundException if the Global Pot is not found.
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
     * @return the number of contributors.
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
     * @return the number of followers.
     */
    @Override
    public int getFollowersCount(final String globalPotId) {
        return globalPotRepository
                .getFollowersCountGlobalByPotId(globalPotId);
    }

    /**
     * Validates the amount contributed to a Global Pot.
     *
     * @param amountContributed the amount contributed
     * @throws InsufficientBalanceException if the amount is null or
     * non-positive.
     */
    @Override
    public void validateAmountContributed(final Double amountContributed) {

        if (amountContributed == null || amountContributed <= 0) {
            throw new InsufficientBalanceException(
                    "Amount contributed is null or negative"
            );
        }

    }

    /**
     * Validates if a user is an admin of the Global Pot.
     *
     * @param admin the user to validate
     * @throws UserNotFoundException if the user is not an admin.
     */
    @Override
    public void validateAdmin(final User admin) {
        if (admin == null) {
            throw new UserNotFoundException(
                    "Admin user not found for the Global Pot"
            );
        }

            if (admin.getUserRole() != UserRoles.GLOBALPOT_ADMIN) {
            throw new UserNotFoundException(
                    "User is not authorized as Global Pot Admin"
            );
        }


    }

    /**
     * Retrieves group chat messages for a given Global Pot from the database.
     *
     * @param globalPotId the ID of the Global Pot
     * @return list of group chat messages.
     */
    @Override
    public List<GroupChatMessage> getGroupChatMessagesFromDB(
            final String globalPotId) {
        return groupChatMessageRepository.
                findByGlobalPotGlobalPotId(globalPotId);
    }


    /**
     * Validates if a user has contributed to a Global Pot.
     *
     * @param globalPotId the ID of the Global Pot
     * @param userUuid    the UUID of the user
     * @throws ResourceNotFoundException if the user has not contributed.
     */
    @Override
    public void validateUserHasContributed(
            final String globalPotId, final String userUuid) {
        if (!contributerRepository
                .existsByGlobalPotGlobalPotIdAndUserContributorUuid(
                        globalPotId, userUuid)) {

            throw new ResourceNotFoundException(
                    "Only contributors can be blocked"
            );
        }
    }

    /**
     * Ensures that a user is a member of a Global Pot.
     *
     * @param userId       the user to be added as a member
     * @param globalPotId  the Global Pot to which the user is to be added.
     */
    @Override
    public void ensureUserIsMember(final User userId,
                                   final GlobalPot globalPotId) {

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

    /**
     * Ensures that a user is a member of a Global Pot by an admin action.
     *
     * @param userId       the user to be added as a member
     * @param globalPotId  the Global Pot to which the user is to be added
     * @throws UserAlreadyExist if the user is already a member of the
     * Global Pot.
     */
    @Override
    public void ensureUserIsMemberByAdmin(final User userId,
                                          final GlobalPot globalPotId) {

        boolean isAlreadyMember =
                globalPotMembersRepository
                        .existsByUserAndGlobalPot(
                                userId,
                                globalPotId);

        if (isAlreadyMember) {
            throw new UserAlreadyExist(
                    "User with uuid [" + userId.getFullName()
                            + "] is already a member of Global Pot ["
                            + globalPotId.getCaseTitle()
                            + "]"
            );
        }

        GlobalPotMembers member = GlobalPotMembers.builder()
                .user(userId)
                .globalPot(globalPotId)
                .userRoles(userId.getUserRole())
                .build();

        globalPotMembersRepository.save(member);
    }

    /**
     * Retrieves a Global Pot member by user UUID and Global Pot ID.
     *
     * @param userUuid    the user UUID
     * @param globalPotId the Global Pot ID
     * @return the Global Pot member
     * @throws ResourceNotFoundException if the member is not found.
     */
    @Override
    public GlobalPotMembers getMemberByUuidAndGlobalPotId(
            final User userUuid,
            final GlobalPot globalPotId) {
        return globalPotMembersRepository.findByUserAndGlobalPot(
                userUuid, globalPotId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Member not found with UUID: "
                                + userUuid.getUuid()
                                + " in Global Pot ID: "
                                + globalPotId.getGlobalPotId()));
    }

    @Override
    public void ensureFollowerAndMember(
            final User user,
            final GlobalPot globalPot,
            final UserRoles roles) {

    }



    @Override
    public GlobalPotDocument getGlobalPotDocumentId(
            final String documentId) {

        return globalPotDocumentRepository.findById(documentId).orElseThrow(() ->
                new GlobalPotDocumentNotFoundException("Document not " +
                        "found with Document Id : " + documentId));
    }

}



