package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.*;

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

    void validateAmountContributed(Double amountContributed);


    /**
     * Validates the admin of the global pot.
     * @param admin
     */
    void validateAdmin(User admin);

    List<GroupChatMessage> getGroupChatMessagesFromDB(String globalPotId);

    void validateUserHasContributed(String globalPotId, String userUuid);

    void ensureUserIsMember(User userId, GlobalPot globalPotId);

    void ensureUserIsMemberByAdmin(User userId, GlobalPot globalPotId);

    GlobalPotMembers getMemberByUuidAndGlobalPotId(
            User userUuid, GlobalPot globalPotId);
}
