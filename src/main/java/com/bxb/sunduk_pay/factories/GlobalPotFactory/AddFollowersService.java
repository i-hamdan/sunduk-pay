package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.FollowerRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddFollowersService implements GlobalPotOperation {

    /**
     * Validation component used to fetch and validate user details.
     */

    private final Validations validations;
    /**
     * Repository for performing Create operations on Global Pot entities.
     */

    private final GlobalPotRepository globalPotRepository;
    /**
     * Repository for managing follower records.
     */

    private final FollowerRepository followerRepository;

    /**
     * Validation component for Global Pot specific validations.
     */
    private final GlobalPotValidations globalPotValidations;


    /**
     * Tells factory that this service handles ADD_FOLLOWER request.
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FOLLOW_POT;
    }

    /**
     * Perform add follower logic.
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) {

        log.info("Follow_Pot request | user={} pot={}",
                request.getFollowerUser(),
                request.getGlobalPotId());

        // Fetch follower user
        User followerUser =
                validations.getUserInfo(request.getFollowerUser());

        // Fetch Global Pot
        GlobalPot globalPot =
                globalPotRepository.findById(request.getGlobalPotId())
                .orElseThrow(() -> new RuntimeException(
                        "Global Pot not found"));


        // Check if already followed
        if (followerRepository
                .existsByFollowerUserAndGlobalPot(followerUser, globalPot)) {
            log.warn("user {} already follows pot {}",
                    followerUser.getUuid(), globalPot.getGlobalPotId());

            return GlobalPotResponse.builder()
                    .message("Already following this Global Pot ")
                    .build();
        }

        // Create and save follower entry
        Follower follower = Follower.builder()
                .followerUser(followerUser)
                .globalPot(globalPot)
                .build();
        followerRepository.save(follower);
        globalPotValidations.ensureUserIsMember(followerUser,globalPot);

        return GlobalPotResponse.builder()
                .message("Gloable Pot Followed Successfully")
                .build();

    }
}
