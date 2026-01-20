package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.*;
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

    /** Default pagination values. */
    private static final int DEFAULT_PAGE = 0;
    /** Default number of records per page. */
    private static final int DEFAULT_SIZE = 10;

    /** unique identifier of the case. */
    private String uuid;

    /** title of the case. */
    private String caseTitle;
    /** category of the case. */
    private CaseCategory caseCategory;
    /** scope of the pot. */
    private PotScope potScope;
    /** requirement type of the case. */
    private CaseRequirementType caseRequirementType;
    /** status of the pot. */
    private PotStatus potStatus;
    /** detailed description of the case. */
    private String description;
    /** target amount to be raised. */
    private Double targetAmount;



    /** address. */
    private String address;
    /** city. */
    private String city;

    /** country. */
    private String country;

    /** target goal amount. */
    private Double goalAmount;
    /** target date to reach the goal amount. */
    private LocalDate goalDate;

    /** list of document files. */
    private List<DocumentWrapper> documentFiles;

    /** verification status of the pot. */
    private Boolean isVerified;
    /** list of admin users. */
    private List<User> administrators;

    /** admin user uuid. */
    private String adminUuid;
    /** target user uuid for admin actions. */
    private String targetUserUuid;

    /** admin note for the pot. */
    private String adminNote;
    /** name of the beneficiary. */
    private String beneficiaryName;
    /** relation to beneficiary. */
    private String relationToBeneficiary;

    /** list of contributor requests. */
    private List<ContributorRequest> contributors;
    /** list of testimonial requests. */
    private List<TestimonialRequest> testimonials;
    /** list of follower requests. */
    private List<FollowerRequest> followers;
    /**
     * type of global pot request.
     */
    private GlobalPotRequestType globalPotRequestType;
    /**
     * global pot id.
     */
    private String globalPotId;
    /**
     * name of the contributor.
     */
    private String contributorName;
    /**
     * amount contributed.
     */
    private Double amountContributed;
    /**
     * user contributor id.
     */
    private String userContributorId;
    /**
     * flag to indicate if the requester is anonymous.
     */
    private Boolean isAnonymous;
    /**
     * flag to indicate if the requester is a user.
     */
    private Boolean isUser;
    /**
     * contributor image file.
     */
    private MultipartFile contributorImage;
    /**
     * follower user id.
     */
    private String followerUser;
    /**
     * wallet id of the source wallet for contribution.
     */
    private String sourceWalletId;
    /**
     * pagination - page number.
     */
    private int pageNumber = DEFAULT_PAGE;
    /**
     * pagination - page size.
     */
    private int pageSize = DEFAULT_SIZE;
    /**
     * filter by admin-created pots.
     */
    private Boolean createdByAdmin;
    /**
     * filter by self-created pots.
     */
    private Boolean createdForSelf;
    /**
     * admin who created the pot.
     */
    private String createdBy;
    /**
     * location for filtering pots.
     */
    private String location;
    /**
     * creator information.
     */
    private Creator creator;

    // target user to add to admin list
    /**
     * target user to add to admin list.
     */
    private String targetUserToAdd;
    /**
     * target user to remove from admin list.
     */
    private String targetUserToRemove;

    /**
     * Document ID for global pot.
     */
    private String globalPotDocumentId;

    /** payment tag for tracking contributions */
    private String paymentTag;

    /** filter for global pot members */
    private GlobalPotMemberFilter globalPotMemberFilter;

    /** type of fetch transaction. */
    private FetchTransactionType fetchTransactionType;

    /** Page number for pagination. */
    private int page = DEFAULT_PAGE;
    /** Number of records per page for pagination. */
    private int size = DEFAULT_SIZE;
    /** Field to sort by (e.g., "dateTime", "amount"). */
    private String sortBy = "dateTime";
    /** Sort direction: ASC or DESC. */
    private String sortDirection = "DESC";
}
