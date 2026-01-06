package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.GlobalPotNotFoundException;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
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


}
