package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.UserRoles;

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
     * @param globalPot
     */
    void validateAdmin(User admin,GlobalPot globalPot);

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

    /**
     * Retrieves a GlobalPotDocument by its document ID.
     *
     * @param documentId the ID of the document
     * @return the GlobalPotDocument object if found
     */
    GlobalPotDocument getGlobalPotDocumentId(String documentId);
    /**
     * Ensures that the user is both a follower and a member of the Global Pot.
     *
     * <p>
     * This method validates the relationship between the user and
     * the Global Pot.
     * It confirms that the user has followed the pot and is also
     * an active member.
     * </p>
     *
     * <p>
     * If the user does not meet either condition, an exception
     * should be thrown by the calling validation logic.
     * </p>
     *
     * @param user       the ID of the user to validate
     * @param globalPot  the ID of the Global Pot
     * @param roles      User Roles
     */
    void ensureFollowerAndMember(User user,
                                 GlobalPot globalPot,
                                 UserRoles roles);


    /**
     * Ensures that the Global Pot has been verified.
     *
     * @param globalPotId the Global Pot to check
     */
    void ensureGlobalPotIsVerified(String globalPotId);

//    /**
//     * Validates that a user has access to a specific Global Pot.
//     *
//     * @param user the user whose access is being validated
//     * @param globalPot the Global Pot for which access is being validated
//     * @throws RuntimeException if the user does not have access to the Global Pot
//     */
//    void validatePotAccess(User user, GlobalPot globalPot);

    /**
     * Validates that the user is both a member of the Global Pot and has admin privileges.
     *
     * @param admin the user to validate
     * @param globalPot the Global Pot to check against
     * @throws RuntimeException if the user is not a member or does not have admin privileges
     */
    void validateSundukPayAndGlobalPotAdmin(User admin, GlobalPot globalPot);

//    GlobalPotResponse validateNonMemberAccess(User user, GlobalPot globalPot);

    /**
     * Validates that a user has full access to a specific Global Pot.
     *
     * @param user the user whose access is being validated
     * @param pot the Global Pot for which access is being validated
     * @return true if the user has full access, false otherwise
     */
    boolean hasFullAccess(User user, GlobalPot pot);





}
