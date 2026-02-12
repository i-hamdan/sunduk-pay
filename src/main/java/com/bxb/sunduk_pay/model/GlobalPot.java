package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.CaseRequirementType;
import com.bxb.sunduk_pay.util.PotScope;
import com.bxb.sunduk_pay.util.PotStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Global Pot fundraising campaign.
 * Stores campaign details, financial progress, and associated
 * verification documents.
 */
@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
//        @Index(name = "idx_created_by", columnList = "admin_user_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
})
public class GlobalPot {

    /** Length constant for description field. */
    private static final int LENGTH_3000 = 3000;

    /** Length constant for case title field. */
    private static final int LENGTH_120 = 120;

    // --- 1. IDENTITY & OWNERSHIP ---

    /** Unique UUID string identifier for the Global Pot. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalPotId;

    /** The digital wallet associated with this
     *  pot for transaction and balance management. */
    @OneToOne
    @JoinColumn(name = "global_wallet_id")
    private GlobalWallet globalWallet;

    // --- 2. CAMPAIGN CORE DETAILS ---

    /** The headline or title of the fundraising case. */
    @Column(length = LENGTH_120)
    private String caseTitle;

    /** The classification category (e.g., Medical, Education) for the case. */
    @Enumerated(EnumType.STRING)
    private CaseCategory caseCategory;

    /** The geographical or social reach of the pot
     *  (Local, National, or Global). */
    @Enumerated(EnumType.STRING)
    private PotScope potScope;

    /** Specific requirement type needed for the case validation. */
    @Enumerated(EnumType.STRING)
    private CaseRequirementType caseRequirementType;

    /** The lifecycle status of the pot
     *  (e.g., PENDING_VERIFICATION, ACTIVE, COMPLETED). */
    @Enumerated(EnumType.STRING)
    private PotStatus potStatus = PotStatus.PENDING_VERIFICATION;

    /** Comprehensive description providing details about
     *  the fundraising cause. */
    @Column(length = LENGTH_3000, columnDefinition = "TEXT")
    private String description;

    // --- 3. BENEFICIARY & LOCATION ---

    /** Legal name of the individual or entity receiving the funds. */
    private String beneficiaryName;

    /** The relationship between the pot creator (admin) and the beneficiary. */
    private String relationToBeneficiary;

    /** Physical address associated with the case for verification purposes. */
    private String address;

    /** City where the case or beneficiary is located. */
    private String city;

    /** State where the case or beneficiary is located. */
    private String state;

    /** Country where the case or beneficiary is located. */
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
    /** URL link to the main image representing the fundraising case. */
    @Builder.Default
    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<GlobalPotDocument> globalPotDocuments = new ArrayList<>();
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

    // --- 1. IDENTITY & OWNERSHIP ---

    /** List of admin users managing this pot. */
    @ManyToMany
    @JoinTable(
            name = "pot_administrators",
            joinColumns = @JoinColumn(name = "global_pot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> administrators;

    // --- 8. AUDIT & METADATA ---

    /** Flag indicating if the pot is currently
     * visible and accepting contributions. */
    private Boolean isActive;

    /** Internal notes provided by system
     *  administrators regarding the pot. */
    private String adminNote;

    /** Indicates if the pot was created by an admin user. */
    private Boolean createdByAdmin;

    /** Indicates if the pot was created for the admin itself. */
    private Boolean createdForSelf;

    /** Name of the person who created the pot. */
    private String createdBy;

    /** Designation of the pot's creator. */
    private String designation;

    /** Geographical location associated with the pot's creator. */
    private String location;

    /** Automatic timestamp of when the pot record was first created. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Automatic timestamp of the last time the pot record was modified. */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * List of members associated with this global pot.
     */
    @Builder.Default
    @OneToMany(mappedBy = "globalPot",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalPotMembers> members = new ArrayList<>();


    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    private List<GlobalPotInteraction> interactions = new ArrayList<>();


    /** Adds a document to the global pot and
     * sets the bidirectional relationship.
     * @param doc The document to be added.
     */
    public void addDocument(
            final GlobalPotDocument doc) {
        if (doc != null) {
            this.globalPotDocuments.add(doc);
            doc.setGlobalPot(this);
        }
    }

}
