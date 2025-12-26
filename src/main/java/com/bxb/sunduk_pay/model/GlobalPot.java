package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a Global Pot fundraising campaign.
 * Stores campaign details, financial progress, and associated verification documents.
 */
@Entity
@Getter
@Setter
@Table(name = "global_pots", indexes = {
        @Index(name = "idx_case_title", columnList = "caseTitle"),
        @Index(name = "idx_description", columnList = "description"),
        @Index(name = "idx_case_category", columnList = "caseCategory"),
        @Index(name = "idx_pot_status", columnList = "potStatus"),
        @Index(name = "idx_case_requirement_type", columnList =
                "caseRequirementType"),
        @Index(name = "idx_goal_amount", columnList = "goalAmount"),
        @Index(name = "idx_goal_date", columnList = "goalDate"),
        @Index(name = "idx_contributed_balance", columnList =
                "contributedBalance"),
        @Index(name = "idx_current_balance", columnList =
                "currentBalance"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_country", columnList = "country"),
        @Index(name = "idx_beneficiary_name", columnList = "beneficiaryName"),
        @Index(name = "idx_relation_to_beneficiary", columnList =
                "relationToBeneficiary"),
        @Index(name = "idx_created_by", columnList = "admin_user_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
})
public class GlobalPot {

    // --- 1. IDENTITY & OWNERSHIP ---

    /** Unique UUID string identifier for the Global Pot. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalPotId;

    /** The administrative user or creator who initiated and manages the pot. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id")
    private User admin;

    /** The digital wallet associated with this
     *  pot for transaction and balance management. */
    @OneToOne
    @JoinColumn(name = "global_wallet_id")
    private GlobalWallet globalWallet;

    // --- 2. CAMPAIGN CORE DETAILS ---

    /** The headline or title of the fundraising case. */
    @Column(nullable = false, length = 120)
    private String caseTitle;

    /** The classification category (e.g., Medical, Education) for the case. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseCategory caseCategory;

    /** The geographical or social reach of the pot
     *  (Local, National, or Global). */
    @Enumerated(EnumType.STRING)
    private PotScope potScope;

    /** Specific requirement type needed for the case validation. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseRequirementType caseRequirementType;

    /** The lifecycle status of the pot
     *  (e.g., PENDING_VERIFICATION, ACTIVE, COMPLETED). */
    @Enumerated(EnumType.STRING)
    private PotStatus potStatus = PotStatus.PENDING_VERIFICATION;

    /** Comprehensive description providing details about
     *  the fundraising cause. */
    @Column(length = 3000, columnDefinition = "TEXT")
    private String description;

    // --- 3. BENEFICIARY & LOCATION ---

    /** Legal name of the individual or entity receiving the funds. */
    @Column(nullable = false)
    private String beneficiaryName;

    /** The relationship between the pot creator (admin) and the beneficiary. */
    @Column(nullable = false)
    private String relationToBeneficiary;

    /** Physical address associated with the case for verification purposes. */
    @Column(nullable = false)
    private String address;

    /** City where the case or beneficiary is located. */
    @Column(nullable = false)
    private String city;

    /** Country where the case or beneficiary is located. */
    @Column(nullable = false)
    private String country;

    // --- 4. FINANCIAL TRACKING ---

    /** The target monetary amount to be raised. */
    private Double goalAmount;

    /** Cumulative amount contributed by all participants. */
    private Double contributedBalance;

    /** Current available balance within
     *  the pot (contributed minus withdrawals). */
    private Double currentBalance;

    /** The deadline date by which the goal amount should ideally be reached. */
    private LocalDate goalDate;

    // --- 5. MEDIA & VISUALS ---

    /** Primary display image for the campaign. Stored as binary data. */

    private String primaryImage;

    /** Secondary gallery image for the campaign. Stored as binary data. */

    private String secondaryImage;

    // --- 6. VERIFICATION DOCUMENTS (KYC & COMPLIANCE) ---

    /** Descriptive title for the Identity/KYC document. */
    private String kycDocumentTitle;

    /** Verification status of the KYC document. */
    @Enumerated(EnumType.STRING)
    private DocumentStatus kycDocumentStatus = DocumentStatus.PENDING;

    /** Binary data for the Identity/KYC document (PDF or Image). */

    private String kycDocument;

    /** Title for institutional or organizational proof. */
    private String institutionDocumentTitle;

    /** Verification status of the institution document. */
    @Enumerated(EnumType.STRING)
    private DocumentStatus institutionDocumentStatus = DocumentStatus.PENDING;

    /** Binary data for the institutional document. */

    private String institutionDocument;

    /** Title for additional supporting evidence. */
    private String supportingDocumentTitle;

    /** Verification status of the supporting document. */
    @Enumerated(EnumType.STRING)
    private DocumentStatus supportingDocumentStatus = DocumentStatus.PENDING;

    /** Binary data for general supporting documents. */

    private String supportingDocument;

    /** Title for any custom or miscellaneous requirement. */
    private String customDocumentTitle;

    /** Verification status of the custom document. */
    @Enumerated(EnumType.STRING)
    private DocumentStatus customDocumentStatus = DocumentStatus.PENDING;

    /** Binary data for custom documents. */

    private String customDocument;

    // --- 7. COLLECTIONS (BIDIRECTIONAL) ---

    /** List of users who have donated to this specific pot. */
    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    private List<Contributor> contributors;

    /** List of feedback or testimonials provided by users for this pot. */
    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    @ToStringExclude
    private List<Testimonial> testimonials;

    /** List of users following this pot for updates. */
    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    private List<Follower> followers;

    // --- 8. AUDIT & METADATA ---

    /** Flag indicating if the pot is currently
     * visible and accepting contributions. */
    private Boolean isActive;

    /** Internal notes provided by system
     *  administrators regarding the pot's validity. */
    private String adminNote;

    /** Automatic timestamp of when the pot record was first created. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Automatic timestamp of the last time the pot record was modified. */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}