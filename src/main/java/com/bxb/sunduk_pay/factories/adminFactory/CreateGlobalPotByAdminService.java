//package com.bxb.sunduk_pay.factories.adminFactory;
//
//import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
//import com.bxb.sunduk_pay.Mappers.TestimonialMapper;
//import com.bxb.sunduk_pay.exception.UserNotFoundException;
//import com.bxb.sunduk_pay.model.GlobalPot;
//import com.bxb.sunduk_pay.model.GlobalPotDocument;
//import com.bxb.sunduk_pay.model.Testimonial;
//import com.bxb.sunduk_pay.model.User;
//import com.bxb.sunduk_pay.repository.GlobalPotRepository;
//import com.bxb.sunduk_pay.repository.UserRepository;
//import com.bxb.sunduk_pay.request.DocumentWrapper;
//import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
//import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
//import com.bxb.sunduk_pay.util.AdminRequestType;
//import com.bxb.sunduk_pay.util.DocumentStatus;
//import com.bxb.sunduk_pay.util.PotScope;
//import com.bxb.sunduk_pay.util.UserRoles;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CreateGlobalPotByAdminService implements SundukPayAdminOperation{
//
//    /** Repository to manage GlobalPot data. */
//    private final GlobalPotRepository repository;
//    /** Mapper to convert between request and entity. */
//    private final GlobalPotMapper mapper;
//    /** Repository to manage User data. */
//    private final UserRepository userRepository;
//
//    private final TestimonialMapper testimonialMapper;
//
//    @Override
//    public AdminRequestType getAdminRequestType() {
//        return AdminRequestType.CREATE_POT;
//    }
//
//    @Override
//    @Transactional
//    public SundukPayAdminResponse perform(SundukPayAdminRequest request) {
//        log.info("Starting Global Pot creation process");
//
//        GlobalPot pot = GlobalPot.builder()
//                .caseTitle(request.getCaseTitle())
//                .caseCategory(request.getCaseCategory())
//                .caseRequirementType(request.getCaseRequirementType())
//                .description(request.getDescription())
//                .potStatus(request.getPotStatus())
//                .potScope(PotScope.PUBLIC)
//                .goalDate(request.getGoalDate())
//                .goalAmount(request.getGoalAmount())
//                .city(request.getCity())
//                .address(request.getAddress())
//                .country(request.getCountry())
//                .beneficiaryName(request.getBeneficiaryName())
//                .relationToBeneficiary(request.getRelationToBeneficiary())
//                .createdByAdmin(request.getCreatedByAdmin())
//                .createdForSelf(request.getCreatedForSelf())
//                .adminNote(request.getAdminNote())
//                .createdBy(request.getCreator().getCreatedBy())
//                .designation(request.getCreator().getDesignation())
//                .location(request.getLocation())
//                .currentBalance(0.0)
//                .contributedBalance(0.0)
//                .administrators(request.getAdministrators())
//                .build();
//
//        // Handle Testimonials
//        if (request.getTestimonials() != null) {
//            pot.setTestimonials(request.getTestimonials().stream()
//                    .map(req -> {
//                        Testimonial t = testimonialMapper.toEntity(req);
//                        t.setGlobalPot(pot);
//                        return t;
//                    }).collect(Collectors.toList()));
//        } else {
//            pot.setTestimonials(new ArrayList<>());
//        }
//
//        // Handle Media Files
//        List<DocumentWrapper> mediaFiles = request.getDocumentFiles();
//        log.info("Saving media files for GlobalPot ID: {}",
//                pot.getGlobalPotId());
//        mediaFiles.forEach((wrapper) -> {
//            GlobalPotDocument globalPotDocument;
//            if (wrapper != null && wrapper.getDocumentFile() != null
//                    && !wrapper.getDocumentFile().isEmpty()) {
//                try {
//                    globalPotDocument =
//                            GlobalPotDocument.builder()
//                                    .documentHeading(
//                                            wrapper.getDocumentHeading())
//                                    .documentTitle(wrapper.getDocumentTitle())
//                                    .document(wrapper.getDocumentFile()
//                                            .getBytes())
//                                    .documentStatus(DocumentStatus.PENDING)
//                                    .globalPot(pot).build();
//                    pot.addDocument(globalPotDocument);
//                    log.info("Saved document: {}",
//                            wrapper.getDocumentHeading());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            else {
//                log.warn("Skipping document '{}' - No file content "
//                        + "found in request", wrapper.getDocumentHeading());
//            }
//        });
//
//        repository.save(pot);
//        log.info("GlobalPot saved successfully with ID: {}",
//                pot.getGlobalPotId());
//
//        //  Build response
//        return SundukPayAdminResponse.builder()
//                .globalPotId(pot.getGlobalPotId())
//                .message("Global pot created successfully")
//                .build();
//    }
//}
