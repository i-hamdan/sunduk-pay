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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddFollowersService implements GlobalPotOperation {

    /**
     * Validation component used to fetch and validate user details.
     */

    private Validations validations;
    /**
     * Repository for performing Create operations on Global Pot entities.
     */

    private GlobalPotRepository globalPotRepository;
    /**
     * Repository for managing follower records.
     */

    private FollowerRepository followerRepository;

    private final GlobalPotValidations globalPotValidations;

    /**
     * Constructs the AddFollowersService with required dependencies.
     *
     * @param validations           validation utility for user-related checks
     * @param globalPotRepository   repository for Global Pot data access
     * @param followerRepository    repository for follower persistence
     */
    public AddFollowersService(Validations validations, GlobalPotRepository globalPotRepository,
                               FollowerRepository followerRepository, GlobalPotValidations globalPotValidations) {
        this.validations = validations;
        this.globalPotRepository = globalPotRepository;
        this.followerRepository = followerRepository;
        this.globalPotValidations = globalPotValidations;
    }

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
    public GlobalPotResponse perform(GlobalPotRequest request) {

        log.info("Follow_Pot request | user={} pot={}",
                request.getFollowerUser(),
                request.getGlobalPotId());

        // Fetch follower user
        User followerUser =
                validations.getUserInfo(request.getFollowerUser());

        // Fetch Global Pot
        GlobalPot globalPot =
                globalPotRepository.findById(request.getGlobalPotId())
                .orElseThrow(() -> new RuntimeException("Global Pot not found"));


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
