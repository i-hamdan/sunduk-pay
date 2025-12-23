package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Data
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

    private String primaryImage;
    private String secondaryImage;

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
    private Long followerCount;

    // --- Status Message ---
    private String message;
    private String status;
}