package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotBlockedUser;
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

    void validateAmountContributed(Double amountContributed);


    /**
     * Validates the admin of the global pot.
     * @param admin
     */
    void validateAdmin(User admin);


    List<GroupChatMessage> getGroupChatMessagesFromDB(String globalPotId);


    void validateUserHasContributed(String globalPotId, String userUuid);

    void validateNotAlreadyBlocked(String globalPotId, String userUuid);


    void validateUserNotBlocked(String globalPotId, String userUuid);


}
