package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.CaseRequirementType;
import com.bxb.sunduk_pay.util.PotScope;
import com.bxb.sunduk_pay.util.PotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for all operations in Global Pot.
 */
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
    // category of the case
    private CaseCategory caseCategory;
    // scope of the pot
    private PotScope potScope;
    // type of case requirement
    private CaseRequirementType caseRequirementType;
    // status of the pot
    private PotStatus PotStatus;
    // detailed description of the case
    private String description;
    // target amount to be collected
    private Double targetAmount;



    // --- Geolocation ---
    private String address;
    // city
    private String city;

    // state or province
    private String country;

    // --- Financials & Dates ---
    private Double goalAmount;
    // date by which the goal amount is to be achieved
    private LocalDate goalDate;

    // --- Binary Media (Images & Docs) ---
    private List<DocumentWrapper> documentFiles;

    // --- Audit & Ownership ---
    private Boolean isVerified;
    // list of admin users
    private List<User> administrators;

    // current admin user creating the pot
    private String adminUuid;
    // target user for transfering pot to user
    private String targetUserUuid;

    // admin note
    private String adminNote;
    // beneficiary name
    private String beneficiaryName;
    // relation to beneficiary
    private String relationToBeneficiary;

    // --- Nested Child Lists ---
    private List<ContributorRequest> contributors;
    // list of photo requests
    private List<TestimonialRequest> testimonials;
    // list of follower requests
    private List<FollowerRequest> followers;
    // type of global pot request
    private GlobalPotRequestType globalPotRequestType;
    // global pot id
    private String globalPotId;
    // contributor name
    private String contributorName;
    // amount contributed by the user
    private Double amountContributed;
    // user contributor id
    private String userContributorId;
    // flag to indicate if the contributor is anonymous
    private Boolean isAnonymous;
    // flag to indicate if the contributor is a registered user
    private Boolean isUser;
    // contributor image file
    private MultipartFile contributorImage;
    // follower user id
    private String followerUser;
    // wallet id of the source wallet for contribution
    private String sourceWalletId;
    // pagination - page number
    private int pageNumber;
    // number of pots per page
    private int pageSize;

    // filter by admin-created pots
    private Boolean createdByAdmin;
    // filter by self-created pots
    private Boolean createdForSelf;
    // filter by creator
    private String createdBy;
    // location filter
    private String location;

    private Creator creator;

    // target user to add to admin list
    private String targetUserToAdd;
    // target user to remove from admin list
    private String targetUserToRemove;

   // for document retrieval
    private String globalPotDocumentId;

}
