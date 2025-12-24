package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // Added for Jackson deserialization
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPotResponse {

    private String globalPotId;

    // --- Core Information ---
    private String caseTitle;
    private String caseCategory;
    private String potScope;
    private String caseRequirementType;
    private String potStatus;
    private String description;
    private List<Testimonial> testimonials;
    private String primaryImage;
    private String secondaryImage;
    private String kycDocumentTitle;
    private String kycDocument;
    private String institutionDocumentTitle;
    private String institutionDocument;
    private String supportingDocumentTitle;
    private String supportingDocument;
    private String customDocumentTitle;
    private String customDocument;

    // --- Geolocation ---
    private String address;
    private String city;
    private String country;

    // --- Financials & Metrics ---
    private Double goalAmount;
    private Double contributedBalance;
    private Double currentBalance;
    private LocalDate goalDate;

    // --- Ownership & Audit ---
    private String admin;
    private String beneficiaryName;
    private String relationToBeneficiary;
    private Boolean isVerified;
    private Boolean isActive;


    private Integer contributorCount;
    private Integer followerCount;

    // --- Status Message ---
    private String message;
    private String status;


// this is list where we show on tile page;
    private List<GlobalPotTileDto> globalPotsTiles;


}