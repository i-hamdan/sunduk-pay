package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.User;

import java.util.List;

/**
 * Interface for validating global pot-related operations.
 */
public interface GlobalPotValidations {

    /**
     * Validates the existence of a global pot by its ID.
     *
     * @param globalPotId the ID of the global pot to validate
     * @return the GlobalPot object if it exists
     * @throws RuntimeException if the global pot does not exist
     */
    GlobalPot getGlobalPot(String globalPotId);

    /**
     * Retrieves the count of contributors for a given global pot.
     *
     * @param globalPotId the ID of the global pot
     * @return the number of contributors
     */
    int getContributorsCount(String globalPotId);

    /**
     * Retrieves the count of followers for a given global pot.
     *
     * @param globalPotId the ID of the global pot
     * @return the number of followers
     */
    int getFollowersCount(String globalPotId);

    /**
     * Validates the amount contributed to a global pot.
     *
     * @param amountContributed the amount to validate
     * @throws RuntimeException if the amount is invalid
     */
    void validateAmountContributed(Double amountContributed);


    /**
     * Validates the admin of the global pot.
     * @param admin
     */
    void validateAdmin(User admin);

    /**
     * Retrieves group chat messages for a given global pot from the database.
     *
     * @param globalPotId the ID of the global pot
     * @return a list of GroupChatMessage objects
     */
    List<GroupChatMessage> getGroupChatMessagesFromDB(String globalPotId);

    /**
     * Validates that a user has contributed to a global pot.
     *
     * @param globalPotId the ID of the global pot
     * @param userUuid the UUID of the user
     * @throws RuntimeException if the user has not contributed
     */
    void validateUserHasContributed(String globalPotId, String userUuid);

    /**
     * Ensures that a user is a member of a global pot.
     *
     * @param userId the user to check
     * @param globalPotId the global pot to check against
     */
    void ensureUserIsMember(User userId, GlobalPot globalPotId);

    /**
     * Ensures that a user is a member of a global pot, with admin privileges.
     *
     * @param userId the user to check
     * @param globalPotId the global pot to check against
     */
    void ensureUserIsMemberByAdmin(User userId, GlobalPot globalPotId);

    /**
     * Retrieves a GlobalPotMembers object by user UUID and global pot ID.
     *
     * @param userUuid the UUID of the user
     * @param globalPotId the ID of the global pot
     * @return the GlobalPotMembers object if found
     */
    GlobalPotMembers getMemberByUuidAndGlobalPotId(
            User userUuid, GlobalPot globalPotId);
}
