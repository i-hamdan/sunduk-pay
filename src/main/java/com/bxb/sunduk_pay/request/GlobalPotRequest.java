package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPotRequest {

    // --- Core Information ---
    private String caseTitle;
    private CaseCategory caseCategory;
    private PotScope potScope;
    private CaseRequirementType caseRequirementType;
    private PotStatus PotStatus;
    private String description;

    // --- Geolocation ---
    private String address;
    private String city;
    private String country;

    // --- Financials & Dates ---
    private Double goalAmount;
    private LocalDate goalDate;

    // --- Document Titles ---
    private String kycDocumentTitle;
    private String institutionDocumentTitle;
    private String supportingDocumentTitle;
    private String customDocumentTitle;

    // --- Binary Media (Images & Docs) ---
    private byte[] primaryImage;
    private byte[] secondaryImage;
    private byte[] kycDocument;
    private byte[] institutionDocument;
    private byte[] supportingDocument;
    private byte[] customDocument;

    // --- Audit & Ownership ---
    private Boolean isVerified;
    private String admin;
    private String beneficiaryName;
    private String relationToBeneficiary;

    // --- Nested Child Lists ---
    private List<ContributorRequest> contributors;
    private List<TestimonialRequest> testimonials;
    private List<FollowerRequest> followers;

    private GlobalPotRequestType globalPotRequestType;
    private String globalPotId;

    private String contributorName;
    private Double amountContributed;
    private String userContributorId;
    private Boolean isAnonymous;
    private Boolean isUser;
    private byte[] contributorImage;
}