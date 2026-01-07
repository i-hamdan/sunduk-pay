package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPotRequest {

    // --- Identification ---
    private String uuid;

    // --- Core Information ---
    private String caseTitle;
    private CaseCategory caseCategory;
    private PotScope potScope;
    private CaseRequirementType caseRequirementType;
    private PotStatus PotStatus;
    private String description;
    private Double targetAmount;



    // --- Geolocation ---
    private String address;
    private String city;
    private String country;

    // --- Financials & Dates ---
    private Double goalAmount;
    private LocalDate goalDate;

    // --- Binary Media (Images & Docs) ---
    private List<DocumentWrapper> documentFiles;

    // --- Audit & Ownership ---
    private Boolean isVerified;

    // current admin user creating the pot
    private String adminUuid;
    // target user for transfering pot to user
    private String targetUserUuid;


    private String adminNote;
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

    private String sourceWalletId;

    private int pageNumber;
    private int pageSize;


}