package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for Global Pot details.
 * This class encapsulates all relevant information
 * about a global pot, including its core details,
 * financials, ownership, and associated metadata.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // Added for Jackson deserialization
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPotResponse {

    /** Unique identifier for the global pot.
     * This field is essential for distinguishing
     * between different global pots in the system.
     */
    private String globalPotId;


    // --- Core Information ---
    /** Title of the global pot.
     * This field represents the name
     * or title given to the global pot.
     */
    private String caseTitle;
    /** Category of the global pot.
     * This field classifies the pot
     * into specific categories such as health,
     * education, disaster relief, etc.
     */
    private String caseCategory;
    /** Scope of the global pot.
     * This field defines whether the pot
     * is local, national, or international in scope.
     */
    private String potScope;
    /** Type of requirement for the global pot.
     * This field specifies whether the pot
     * is intended for medical, educational,
     * disaster relief, or other types of needs.
     */
    private String caseRequirementType;
    /** Current status of the global pot.
     * This field indicates whether the pot is active,
     * completed, or closed.
     */
    private String potStatus;
    /** Detailed description of the global pot.
     * This field provides comprehensive information
     * about the purpose, goals, and background
     * of the global pot.
     */
    private String description;
    /** List of testimonials associated with the global pot.
     * This field provides social proof and credibility
     * for the global pot by showcasing feedback
     * from previous contributors or beneficiaries.
     */
    private List<Testimonial> testimonials;

    // --- Geolocation ---
    /** Address where the global pot is based.
     * This field is important for identifying
     * the specific location of the pot.
     */
    private String address;

    /** City where the global pot is based.
     * This field is important for identifying
     * the geographical location of the pot.
     */
    private String city;
    /** Country where the global pot is based.
     * This field is important for identifying
     * the geographical location of the pot.
     */
    private String country;

    // --- Financials & Metrics ---
    /** Fundraising goal amount
     * for the global pot.
     * This field is essential for defining
     * the target amount to be raised.
     */
    private Double goalAmount;

    /** Total amount contributed
     * to the global pot so far.
     * This field is crucial for tracking
     * the progress towards the fundraising goal.
     */
    private Double contributedBalance;

    /** Current balance of the global pot.
     * This field represents the total amount
     * of funds currently available in the pot.
     */
    private Double currentBalance;

    /** Target date for achieving the fundraising goal.
     * This field is important for setting deadlines
     * and motivating contributors to reach the goal on time.
     */
    private LocalDate goalDate;

    // --- Ownership & Audit ---
    /** Identifier of the admin/creator
     * of the global pot.
     * This field is essential for tracking
     * who initiated and manages the pot.
     */
    private String admin;
    /** Name of the beneficiary
     * who will receive the funds from the global pot.
     * This field is crucial for identifying
     * the intended recipient of the collected funds.
     */
    private String beneficiaryName;
    /** Relationship of the creator/admin
     * to the beneficiary of the global pot.
     * This field helps to clarify the connection
     * between the pot creator and the beneficiary.
     */
    private String relationToBeneficiary;

    /** Indicates whether the global pot has been verified.
     * This field helps to establish the credibility
     * and authenticity of the global pot.
     */
    private Boolean isVerified;

    /** Indicates whether the global pot is currently active.
     * This field helps to determine if the pot is open for contributions
     * or has been closed.
     */
    private Boolean isActive;


    /** Number of contributors to the global pot.
     * This field indicates how many unique users
     * have contributed to the global pot.
     */
    private Integer contributorCount;
    /** Number of followers for the global pot.
     * This field indicates how many users are following
     * or interested in the global pot.
     */
    private Integer followerCount;


    /**
     * Message providing additional information about the global pot response.
     * This field can be used to convey success, error,
     * or informational messages.
     */
    private String message;

    /**
     * Status of the global pot response.
     * This field indicates the current state or outcome
     * of the global pot operation.
     */
    private String status;

    /**
     * List of global pot tile DTOs associated with the global pot.
     * These tiles provide summarized information about the pot.
     */
    private List<GlobalPotTileDto> globalPotsTiles;

    /**
     * List of admin users associated with the global pot.
     * These users have administrative privileges over the pot.
     */
    private List<UserResponse> adminList;

    /**
     * List of group chat history associated with the global pot.
     * contains messages and transactions related to the pot.
     * messages and transactions both are represented by this DTO.
     */
    private List<GroupChatUnifiedDTO> groupChatHistory;

    /**
     * List of document responses associated with the global pot.
     * These documents are used for verification and transparency purposes.
     */
    private List<GlobalPotDocumentResponse> globalPotDocumentResponses;

    /** Full name of the user associated with the global pot.
     * This field provides the complete name of the user.
     */
    private List<FetchGlobalMembersDTO> fetchGlobalMembersDTOS;
}
