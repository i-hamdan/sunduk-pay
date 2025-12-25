package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
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
    private MultipartFile primaryImage;
    private MultipartFile secondaryImage;
    private MultipartFile kycDocument;
    private MultipartFile institutionDocument;
    private MultipartFile supportingDocument;
    private MultipartFile customDocument;

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
    private MultipartFile contributorImage;
    private String followerUser;

    private PotStatus potStatus;

    private int pageNumber;
    private int pageSize;

}