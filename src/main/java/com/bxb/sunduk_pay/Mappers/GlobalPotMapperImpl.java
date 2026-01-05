package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.request.GroupChatMessageRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GlobalPotMapperImpl implements GlobalPotMapper {
    private final TestimonialMapper testimonialMapper;
    private final UserRepository userRepository;

    /**
     * Mapper for User entities.
     */
    private final UserMapper userMapper;


    @Value("${media.base-url}")
    private String mediaBaseUrl;

    /**
     * Converts the incoming request into a persistence-ready Entity.
     */
    public GlobalPot toEntity(GlobalPotRequest request) throws IOException {
        if (request == null) return null;

        GlobalPot pot = new GlobalPot();

        // --- 1. Core Information ---
        pot.setCaseTitle(request.getCaseTitle());
        pot.setCaseCategory(request.getCaseCategory());
        pot.setPotScope(request.getPotScope());
        pot.setCaseRequirementType(request.getCaseRequirementType());
        pot.setPotStatus(request.getPotStatus());
        pot.setDescription(request.getDescription());
//        User admin = userRepository.findById(request.getAdmin())
//                .orElseThrow(() -> new UserNotFoundException("Admin user not " +
//                        "found with ID: " + request.getAdmin()));
//        pot.setAdmin(admin);
        pot.setBeneficiaryName(request.getBeneficiaryName());
        pot.setRelationToBeneficiary(request.getRelationToBeneficiary());

        // --- 2. Geolocation ---
        pot.setAddress(request.getAddress());
        pot.setCity(request.getCity());
        pot.setCountry(request.getCountry());

        // --- 3. Financials & Dates ---
        pot.setContributedBalance(0.0);
        pot.setCurrentBalance(0.0);
        pot.setGoalDate(request.getGoalDate());
        pot.setGoalAmount(request.getGoalAmount());

//        // --- 4. Media Mapping (Private Helper) ---
//        mapMedia(pot, request);

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


    @Override
    public GlobalPotResponse toGlobalPotResponse(GlobalPot pot) {

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



    public GlobalWallet toEntityWallet(final GlobalPotRequest request) {

        GlobalWallet wallet = new GlobalWallet();

        wallet.setBalance(0.0);
        wallet.setIsActive(true);

        return wallet;
    }

    /**
     * @param request
     */
    @Override
    public Contributor toContributerEntity(GlobalPotRequest request,
                                           GlobalPot pot) throws IOException {
        Contributor contributor = new Contributor();
        contributor.setName(request.getContributorName());
        contributor.setAmountContributed(request.getAmountContributed());
        contributor.setIsAnonymous(request.getIsAnonymous());
        User userContributor = userRepository
                .findById(request.getUserContributorId()).orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: "
                                + request.getUserContributorId()));
        contributor.setUserContributor(userContributor);

//        contributor.setProfileImage(request.getContributorImage().getBytes());
        contributor.setIsUser(request.getIsUser());
        contributor.setGlobalPot(pot);
        return contributor;
    }


//    private void mapMedia(GlobalPot pot, GlobalPotRequest request) throws IOException {
//
//        if (request.getPrimaryImage() != null && !request.getPrimaryImage().isEmpty()) {
//            pot.setPrimaryImage(request.getPrimaryImage().getBytes());
//        }
//
//        if (request.getSecondaryImage() != null && !request.getSecondaryImage().isEmpty()) {
//            pot.setSecondaryImage(request.getSecondaryImage().getBytes());
//        }
//
//        if (request.getKycDocument() != null && !request.getKycDocument().isEmpty()) {
//            pot.setKycDocument(request.getKycDocument().getBytes());
//            pot.setKycDocumentTitle(request.getKycDocumentTitle());
//        }
//
//        if (request.getInstitutionDocument() != null && !request.getInstitutionDocument().isEmpty()) {
//            pot.setInstitutionDocument(request.getInstitutionDocument().getBytes());
//            pot.setInstitutionDocumentTitle(request.getInstitutionDocumentTitle());
//        }
//
//        if (request.getSupportingDocument() != null && !request.getSupportingDocument().isEmpty()) {
//            pot.setSupportingDocument(request.getSupportingDocument().getBytes());
//            pot.setSupportingDocumentTitle(request.getSupportingDocumentTitle());
//        }
//
//        if (request.getCustomDocument() != null && !request.getCustomDocument().isEmpty()) {
//            pot.setCustomDocument(request.getCustomDocument().getBytes());
//            pot.setCustomDocumentTitle(request.getCustomDocumentTitle());
//        }
//    }



    @Override
    public GlobalPotTileDto toTileDto(GlobalPot pot) {

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
    public List<GlobalPotTileDto> toTileDtos(List<GlobalPot> pots) {

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
                .content(request.getContent()).
                build();
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
        return GroupChatMessageResponse.builder()
                .messageId(groupChatMessage.getMessageId())
                .sender(userMapper.toUserResponse(groupChatMessage.getSender()))
                .globalPotId(groupChatMessage.getGlobalPot().getGlobalPotId())
                .content(groupChatMessage.getContent())
                .timestamp(groupChatMessage.getTimestamp().toString()).build();
    }


    /**
     * Private helper to convert byte array to base64 string.
     * @param image
     * @return
     */
    // helper method to  set images into base 64
    private String toBase64(byte[] image) {
        if (image == null) return null;
        return Base64.getEncoder().encodeToString(image);
    }


    /**
     * Private helper to map media files from request to entity.
     */
    private void mapMedia(GlobalPot pot, GlobalPotRequest request) throws IOException {

    }


}