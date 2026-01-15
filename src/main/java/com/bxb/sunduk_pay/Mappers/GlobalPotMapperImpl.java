package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.request.GroupChatMessageRequest;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of GlobalPotMapper to map between
 * GlobalPot entities and various request/response objects.
 */
@Component
@RequiredArgsConstructor
public class GlobalPotMapperImpl implements GlobalPotMapper {
    /**
     * Mapper for Testimonial entities.
     */
    private final TestimonialMapper testimonialMapper;
    /**
     * Repository for User entities.
     */
    private final UserRepository userRepository;

    /**
     * Mapper for User entities.
     */
    private final UserMapper userMapper;

    /**
     * Base URL for media resources.
     */
    @Value("${media.base-url}")
    private String mediaBaseUrl;

    /**
     * Converts the incoming request into a persistence-ready Entity.
     *
     * @param request the incoming request
     * @return the persistence-ready entity
     * @throws IOException in case of I/O errors
     */
    public GlobalPot toEntity(
            final GlobalPotRequest request) throws IOException {
        if (request == null) {
            return null;
        }

        GlobalPot pot = new GlobalPot();

        // --- 1. Core Information ---
        pot.setCaseTitle(request.getCaseTitle());
        pot.setCaseCategory(request.getCaseCategory());
        pot.setPotScope(request.getPotScope());
        pot.setCaseRequirementType(request.getCaseRequirementType());
        pot.setPotStatus(request.getPotStatus());
        pot.setDescription(request.getDescription());

       pot.setAdministrators(request.getAdministrators());
       pot.setBeneficiaryName(request.getBeneficiaryName());
       pot.setRelationToBeneficiary(request.getRelationToBeneficiary());
       pot.setCreatedByAdmin(request.getCreatedByAdmin());
       pot.setCreatedForSelf(request.getCreatedForSelf());
       pot.setCreatedBy(request.getCreator().getCreatedBy());
       pot.setDesignation(request.getCreator().getDesignation());
       pot.setLocation(request.getLocation());

        // --- 2. Geolocation ---
        pot.setAddress(request.getAddress());
        pot.setCity(request.getCity());
        pot.setCountry(request.getCountry());

        // --- 3. Financials & Dates ---
        pot.setContributedBalance(0.0);
        pot.setCurrentBalance(0.0);
        pot.setGoalDate(request.getGoalDate());
        pot.setGoalAmount(request.getGoalAmount());

        // --- 5. Empty List Initialization (Defensive) ---
//        pot.setContributors(new ArrayList<>());
//        pot.setFollowers(new ArrayList<>());

        // --- 6. Remaining Children (Example: Testimonials) ---
        if (request.getTestimonials() != null) {
            pot.setTestimonials(request.getTestimonials().stream()
                    .map(req -> {
                        Testimonial t = testimonialMapper.toEntity(req);
                        t.setGlobalPot(pot);
                        return t;
                    }).collect(Collectors.toList()));
        } else {
            pot.setTestimonials(new ArrayList<>());
        }

        return pot;
    }


//    public void updateEntity(GlobalPot pot, GlobalPotRequest request) {

//        pot.setCaseTitle(request.getCaseTitle());
//        pot.setCategory(request.getCategory());
//        pot.setRequirementType(request.getRequirementType());
//        pot.setStatus(request.getStatus());
//
//
//        pot.setDescription(request.getDescription());
//        pot.setTargetAmount(request.getTargetAmount());
//
//        pot.setTargetStartDate(request.getTargetStartDate());
//        pot.setTargetEndDate(request.getTargetEndDate());
//
//        if (request.getPrimaryImage() != null) {
//            pot.setPrimaryImage(request.getPrimaryImage());
//        }
//        if (request.getSecondaryImage() != null) {
//            pot.setSecondaryImage(request.getSecondaryImage());
//        }
//        if (request.getDocument() != null) {
//            pot.setDocument(request.getDocument());
//        }
//
//        pot.setIsVerified(request.getIsVerified());
//        pot.setBeneficiary(request.getBeneficiary());
//        pot.setTestimonial(request.getTestimonial());
//
//        pot.setUpdatedAt(LocalDateTime.now());
//    }


    /**
     * @param pot
     * @return
     */
    @Override
    public GlobalPotResponse toGlobalPotResponse(final GlobalPot pot) {

        return GlobalPotResponse.builder()
                .globalPotId(pot.getGlobalPotId())
                .caseTitle(pot.getCaseTitle())
                .caseCategory(pot.getCaseCategory().toString())
                .caseRequirementType(pot.getCaseRequirementType().toString())
                .potScope(pot.getPotScope().toString())
                .description(pot.getDescription())


                .address(pot.getAddress())
                .city(pot.getCity())
                .country(pot.getCountry())

                .goalAmount(pot.getGoalAmount())
                .contributedBalance(pot.getContributedBalance())
                .currentBalance(pot.getCurrentBalance())
                .goalDate(pot.getGoalDate())

                .beneficiaryName(pot.getBeneficiaryName())
                .relationToBeneficiary(pot.getRelationToBeneficiary())

                .isVerified(false)

                .isActive(pot.getIsActive())

                .message("Global Pot created successfully").build();
    }



    /**
     * @param request
     * @return
     */
    @Override
    public GlobalWallet toEntityWallet(final SundukPayAdminRequest request) {

        GlobalWallet wallet = new GlobalWallet();

        wallet.setBalance(0.0);
        wallet.setIsActive(true);

        return wallet;
    }

    /**
     * @param request
     */
    @Override
    public Contributor toContributerEntity(
            final GlobalPotRequest request,
            final GlobalPot pot) throws IOException {
        Contributor contributor = new Contributor();
        contributor.setName(request.getContributorName());
        contributor.setAmountContributed(request.getAmountContributed());
        contributor.setIsAnonymous(request.getIsAnonymous());
        User userContributor = userRepository
                .findById(request.getUserContributorId()).orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: "
                                        + request.getUserContributorId()));
        contributor.setUserContributor(userContributor);

//        contributor.setProfileImage(request.getContributorImage().getBytes());
        contributor.setIsUser(request.getIsUser());
        contributor.setGlobalPot(pot);
        return contributor;
    }


    /**
     * Converts a GlobalPot entity to a GlobalPotTileDto.
     *
     * @param pot the GlobalPot entity
     * @return the corresponding GlobalPotTileDto
     */
    @Override
    public GlobalPotTileDto toTileDto(final GlobalPot pot) {

        return GlobalPotTileDto.builder()
                .globalPotId(pot.getGlobalPotId())
                .caseTitle(pot.getCaseTitle())
                .caseCategory(
                        pot.getCaseCategory() != null
                                ? pot.getCaseCategory().name()
                                : null
                )
                .isVerified(false)
                .isActive(pot.getIsActive())

                // --- Location ---
                .city(pot.getCity())
                .country(pot.getCountry())

                // --- Financials ---
                .currentBalance(pot.getCurrentBalance())
                .goalAmount(pot.getGoalAmount())
                //.description(pot.getDescription())
               // .caseRequirementType(pot.getCaseRequirementType().toString())


                .contributorCount(
                        pot.getContributors() != null
                                ? pot.getContributors().size()
                                : 0
                )
                .followerCount(
                        pot.getFollowers() != null
                                ? pot.getFollowers().size()
                                : 0
                )
                .build();
    }


    // method to add list on that

    /**
     * Converts a list of GlobalPot entities to a list of GlobalPotTileDto.
     *
     * @param pots the list of GlobalPot entities
     * @return the corresponding list of GlobalPotTileDto
     */
    @Override
    public List<GlobalPotTileDto> toTileDtos(final List<GlobalPot> pots) {

        List<GlobalPotTileDto> responses = new ArrayList<>();

        for (GlobalPot pot : pots) {
            responses.add(toTileDto(pot));
        }
        return responses;
    }

    /**
     * Converts a GroupChatMessageRequest to a GroupChatEvent.
     *
     * @param request the group chat message request
     * @return the corresponding group chat event
     */
    @Override
    public GroupChatEvent toGroupChatEvent(
            final GroupChatMessageRequest request) {
        return GroupChatEvent.builder().
                senderId(request.getSenderId())
                .globalPotId(request.getGlobalPotId())
                .content(request.getContent())
                .isAnonymous(request.getIsAnonymous())
                .build();
    }

    /**
     * Converts a GroupChatMessage to a GroupChatMessageResponse.
     *
     * @param groupChatMessage the group chat message model
     * @return the corresponding group chat message response
     */
    @Override
    public GroupChatMessageResponse toGroupChatMessageResponse(
            final GroupChatMessage groupChatMessage) {
        GroupChatMessageResponse response = new GroupChatMessageResponse();
        response.setMessageId(groupChatMessage.getMessageId());
//        if (groupChatMessage.isAnonymous()) {
//            response.setSender(anonymousSender());
//        } else {
        response.setSender(userMapper
                .toUserResponse(groupChatMessage.getSender()));
//        }
        response.setGlobalPotId(
                groupChatMessage.getGlobalPot().getGlobalPotId());
        response.setContent(groupChatMessage.getContent());
        response.setTimestamp(groupChatMessage.getTimestamp().toString());
        response.setIsAnonymous(groupChatMessage.isAnonymous());
        response.setAnonymousId(groupChatMessage.getAnonymousId());
        response.setAnonymousColor(groupChatMessage.getAnonymousColor());

        return response;
    }


    /**
     * Converts a GroupChatMessage entity to a GroupChatMessageResponse DTO.
     *
     * @param groupChatMessage the group chat message entity
     * @return the corresponding group chat message response DTO
     */
    @Override
    public List<GroupChatMessageResponse> toGroupChatMessageResponseList(
            final List<GroupChatMessage> groupChatMessage) {
        return groupChatMessage
                .stream()
                .map(this::toGroupChatMessageResponse)
                .collect(Collectors.toList());
    }


    /**
     * Private helper to convert byte array to base64 string.
     *
     * @param image byte array
     * @return base64 string
     */
    // helper method to  set images into base 64
    public String toBase64(final byte[] image) {
        if (image == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(image);
    }

    private UserResponse anonymousSender() {
        return UserResponse.builder()
                .fullName("Anonymous user")
                .email("anonymous@gmail.com")
                .build();
    }


}
