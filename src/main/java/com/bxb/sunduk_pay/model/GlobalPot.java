package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.*;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "global_pots")
public class GlobalPot {

    // --- 1. IDENTITY & OWNERSHIP ---

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String globalPotId;

    /** The User (Admin/Creator) who initiated the pot. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id")
    private User admin;

    /** Associated wallet for managing financial transactions. */
    @OneToOne
    @JoinColumn(name = "global_wallet_id")
    private GlobalWallet globalWallet;


    // --- 2. CORE INFORMATION ---

    @Column(nullable = false, length = 120)
    private String caseTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseCategory caseCategory;

    @Enumerated(EnumType.STRING)
    private PotScope potScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseRequirementType caseRequirementType;

    @Enumerated(EnumType.STRING)
    private PotStatus potStatus = PotStatus.PENDING_VERIFICATION;

    @Column(length = 3000)
    private String description;


    // --- 3. BENEFICIARY & GEOLOCATION ---
    @Column(nullable = false)
    private String beneficiaryName;

    @Column(nullable = false)
    private String relationToBeneficiary;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;


    // --- 4. FINANCIALS & DATES ---

    private Double goalAmount;

    private Double contributedBalance = 0.0;

    private Double currentBalance = 0.0;

    private LocalDate goalDate;

    // --- 5. MEDIA & DOCUMENTS (Organized by Type) ---

    /* Promotional Images */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] primaryImage;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] secondaryImage;

    /* KYC Document Group */
    private String kycDocumentTitle;
    @Enumerated(EnumType.STRING)
    private DocumentStatus kycDocumentStatus = DocumentStatus.PENDING;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] kycDocument;

    /* Institution Document Group */
    private String institutionDocumentTitle;
    @Enumerated(EnumType.STRING)
    private DocumentStatus institutionDocumentStatus = DocumentStatus.PENDING;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] institutionDocument;

    /* Supporting Document Group */
    private String supportingDocumentTitle;
    @Enumerated(EnumType.STRING)
    private DocumentStatus supportingDocumentStatus = DocumentStatus.PENDING;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] supportingDocument;

    /* Custom Document Group */
    private String customDocumentTitle;
    @Enumerated(EnumType.STRING)
    private DocumentStatus customDocumentStatus = DocumentStatus.PENDING;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] customDocument;


    // --- 6. COLLECTIONS (Bidirectional) ---

    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    private List<Contributor> contributors;

    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    @ToStringExclude
    private List<Testimonial> testimonials;

    @OneToMany(mappedBy = "globalPot", cascade = CascadeType.ALL)
    private List<Follower> followers;


    // --- 7. ADMIN AUDIT & METADATA ---

//    private Boolean isVerified = false;

    private Boolean isActive = true;

    private String adminNote;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}