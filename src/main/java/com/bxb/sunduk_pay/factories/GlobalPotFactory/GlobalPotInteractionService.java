package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotInteraction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotInteractionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Service to handle user interactions with a global pot.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class GlobalPotInteractionService implements GlobalPotOperation {

    /**
     * Validation component used to fetch and validate user details.
     */
    private final Validations validations;
    /**
     * Validation component for Global Pot related operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * Repository used to manage Global Pot interactions.
     */
    private final GlobalPotInteractionRepository globalPotInteractionRepository;


    /**
     * Returns the Global Pot request type handled by this service.
     *
     * @return POT_INTERACTION request type
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.POT_INTERACTION;
    }

    /**
     * Records a user's interaction with a global pot.
     *
     * @param request the request containing user UUID,
     *                global pot ID, and interaction details
     * @return GlobalPotResponse indicating the result of the operation
     * @throws IOException if an I/O error occurs during the operation
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {
        log.info("Performing Global Pot Interaction Operation");
        User user = validations.getUserInfo(request.getUuid());
        log.info("User validated: " + user.getUuid());


        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());
        log.info("Global Pot validated: " + globalPot.getGlobalPotId());

        GlobalPotInteraction interaction = globalPotInteractionRepository
                .findByUuidAndGlobalPot(user, globalPot)
                .orElseGet(() -> GlobalPotInteraction.builder()
                        .uuid(user)
                        .globalPot(globalPot)
                        .caseCategory(globalPot.getCaseCategory())
                        .visitCount(0)
                        .contributionCount(0)
                        .messageCount(0)
                        .totalContributedAmount(0d)
                        .totalTimeSpentInSeconds(0)
                        .lastVisitedAt(LocalDateTime.now())
                        .build());

        interaction.setVisitCount(interaction.getVisitCount() + 1);
        log.info("Updated visit count: " + interaction.getVisitCount());
        interaction.setTotalTimeSpentInSeconds(
                interaction.getTotalTimeSpentInSeconds() +
                        request.getTimeStampInSeconds()
        );
        log.info("Updated total time spent: " +
                interaction.getTotalTimeSpentInSeconds());
        interaction.setLastVisitedAt(LocalDateTime.now());

        log.info("Set last visited at: " + interaction.getLastVisitedAt());
        interaction.setLastVisitedAt(LocalDateTime.now());

        globalPotInteractionRepository.save(interaction);
        log.info("Global Pot interaction recorded successfully for user: "
                + user.getUuid() + " and Global Pot: "
                + globalPot.getGlobalPotId());
        return GlobalPotResponse.builder()
                .message("Interaction recorded successfully")
                .build();
    }
}
