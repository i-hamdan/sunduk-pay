package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


/**
 * Request DTO used by admin users to perform operations
 * related to Sunduk Pay global pots.
 * This request supports pot management, contributor handling,
 * follower management, document uploads, and pagination.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SundukPayAdminRequest {

    /**
     * Unique UUID of the admin performing the operation.
     */
    private String adminUuid;

    /**
     * Type of admin request being performed.
     */
    private AdminRequestType adminRequestType;

    /**
     * Unique identifier of the global pot.
     */
    private String globalPotId;

    /**
     * Document ID for global pot.
     */
    private String globalPotDocumentId;

    /** title of the case */
    private String caseTitle;
    /** category of the case */
    private CaseCategory caseCategory;
    /** scope of the pot */
    private PotScope potScope;
    /** requirement type of the case */
    private CaseRequirementType caseRequirementType;
    /** status of the pot */
    private PotStatus PotStatus;
    /** detailed description of the case */
    private String description;
    /** target amount to be raised */
    private Double targetAmount;
    /** address */
    private String address;
    /** city */
    private String city;

    /** country */
    private String country;

    /** target goal amount */
    private Double goalAmount;
    /** target date to reach the goal amount */
    private LocalDate goalDate;

    /** list of document files */
    private List<DocumentWrapper> documentFiles;

    /** verification status of the pot */
    private Boolean isVerified;
    /** list of admin users */
    private List<User> administrators;
    /** target user uuid for admin actions */
    private String targetUserUuid;
    /** admin note for the pot */
    private String adminNote;
    /** name of the beneficiary */
    private String beneficiaryName;
    /** relation to beneficiary */
    private String relationToBeneficiary;

    /** list of contributor requests */
    private List<ContributorRequest> contributors;
    /** list of testimonial requests */
    private List<TestimonialRequest> testimonials;
    /** list of follower requests */
    private List<FollowerRequest> followers;
    /**
     * type of global pot request
     */
    private GlobalPotRequestType globalPotRequestType;
    /**
     * name of the contributor
     */
    private String contributorName;
    /**
     * amount contributed
     */
    private Double amountContributed;
    /**
     * user contributor id
     */
    private String userContributorId;
    /**
     * flag to indicate if the requester is anonymous
     */
    private Boolean isAnonymous;
    /**
     * flag to indicate if the requester is a user
     */
    private Boolean isUser;
    /**
     * contributor image file
     */
    private MultipartFile contributorImage;
    /**
     * follower user id
     */
    private String followerUser;
    /**
     * wallet id of the source wallet for contribution
     */
    private String sourceWalletId;
    /**
     * pagination - page number
     */
    private int pageNumber;
    /**
     * pagination - page size
     */
    private int pageSize;
    /**
     * filter by admin-created pots
     */
    private Boolean createdByAdmin;
    /**
     * filter by self-created pots
     */
    private Boolean createdForSelf;
    /**
     * admin who created the pot
     */
    private String createdBy;
    /**
     * location for filtering pots
     */
    private String location;

    private Creator creator;
    /**
     * target user to add to admin list
     */
    private String targetUserToAdd;
    /**
     * target user to remove from admin list
     */
    private String targetUserToRemove;

}
