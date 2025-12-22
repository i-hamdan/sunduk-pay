package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GlobalPotMapperImpl implements GlobalPotMapper{

    private final TestimonialMapper testimonialMapper;
    private final UserRepository userRepository;

    /**
     * Converts the incoming request into a persistence-ready Entity.
     */
    public GlobalPot toEntity(GlobalPotRequest request) {
        if (request == null) return null;

        GlobalPot pot = new GlobalPot();

        // --- 1. Core Information ---
        pot.setCaseTitle(request.getCaseTitle());
        pot.setCaseCategory(request.getCaseCategory());
        pot.setPotScope(request.getPotScope());
        pot.setCaseRequirementType(request.getCaseRequirementType());
        pot.setPotStatus(request.getPotStatus());
        pot.setDescription(request.getDescription());
        User admin = userRepository.findById(request.getAdmin())
                .orElseThrow(() -> new UserNotFoundException("Admin user not " +
                        "found with ID: " + request.getAdmin()));
        pot.setAdmin(admin);
        pot.setBeneficiaryName(request.getBeneficiaryName());
        pot.setRelationToBeneficiary(request.getRelationToBeneficiary());

        // --- 2. Geolocation ---
        pot.setAddress(request.getAddress());
        pot.setCity(request.getCity());
        pot.setCountry(request.getCountry());

        // --- 3. Financials & Dates ---
        pot.setContributedBalance(0.0);
        pot.setCurrentBalance(0.0);
        pot.setGoalAmount(request.getGoalAmount());

        // --- 4. Media Mapping (Private Helper) ---
        mapMedia(pot, request);

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


    public void updateEntity(GlobalPot pot, GlobalPotRequest request) {

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
    }

    /**
     * @param pot
     * @return
     */
    @Override
    public GlobalPotRequest toRequest(GlobalPot pot) {
        return null;
    }


    public GlobalPotResponse toResponse(GlobalPot pot) {

        GlobalPotResponse res = new GlobalPotResponse();
        res.setStatus("Global Pot created successfully");
        return res;
    }

    /**
     * @param wallet
     * @return
     */
    @Override
    public GlobalPotResponse toResponse(GlobalWallet wallet) {
       GlobalPotResponse res = new GlobalPotResponse();
       res.setStatus("Global Wallet created successfully");
       return res;
    }

    public GlobalWallet toEntityWallet(final GlobalPotRequest request) {

        GlobalWallet wallet = new GlobalWallet();

        wallet.setBalance(0.0);
        wallet.setIsActive(true);
        wallet.setCreatedAt(LocalDateTime.now());

        return wallet;
    }
    private void mapMedia(GlobalPot pot, GlobalPotRequest request) {
        pot.setPrimaryImage(request.getPrimaryImage());
        pot.setSecondaryImage(request.getSecondaryImage());

        pot.setKycDocument(request.getKycDocument());
        pot.setKycDocumentTitle(request.getKycDocumentTitle());

        pot.setInstitutionDocument(request.getInstitutionDocument());
        pot.setInstitutionDocumentTitle(request.getInstitutionDocumentTitle());

        pot.setSupportingDocument(request.getSupportingDocument());
        pot.setSupportingDocumentTitle(request.getSupportingDocumentTitle());

        pot.setCustomDocument(request.getCustomDocument());
        pot.setCustomDocumentTitle(request.getCustomDocumentTitle());
    }
}
