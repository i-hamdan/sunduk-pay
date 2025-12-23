package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.GlobalPotNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class GlobalPotValidationsImpl implements GlobalPotValidations{

    private final GlobalPotRepository globalPotRepository;


    /**
     * Validates the existence of a Global Pot by its ID.
     *
     * @param globalPotId the ID of the Global Pot to validate
     * @return the validated Global Pot
     * @throws GlobalPotNotFoundException if the Global Pot is not found
     */
    @Override
    public GlobalPot getGlobalPot(String globalPotId) {
        GlobalPot globalPot =
                globalPotRepository.findById(globalPotId).orElseThrow(
                        ()-> new GlobalPotNotFoundException(
                                "Global Pot not found with ID: "
                                        + globalPotId));
        return globalPot;

    }

    /**
     * Retrieves the count of contributors for a given Global Pot.
     *
     * @param globalPotId the ID of the Global Pot
     * @return the number of contributors
     */
    @Override
    public int getContributorsCount(String globalPotId) {
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
    public int getFollowersCount(String globalPotId) {
        return globalPotRepository
                .getFollowersCountGlobalByPotId(globalPotId);
    }
}
