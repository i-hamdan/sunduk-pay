package com.bxb.sunduk_pay.model;
import com.bxb.sunduk_pay.util.UserRoles;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;


/**
 * Represents a user in the system.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
/**
* Full name of the user.
 */
    private String fullName;
    /**
     * Gender of the user.
     */
    private String gender;
    /**
     * Email address of the user.
     */
    private String email;
    /**
     * Phone number of the user.
     */
    private String phoneNumber;
    /**
     * Hashed password for user authentication.
     */
    private String password;
    /**
     * Indicates if the user account is deleted.
     */
    private Boolean isDeleted;
    /**
     * DOb of User.
     */
    private String dateOfBirth;
    /**
     * present adress of User.
      */
    private String presentAddress;
    /**
     * permanent address of USer.
     */
    private String permanentAddress;
    /**
     * country of User.
     */
    private String country;

    /**
     * state of User.
     */
    private String state;
    /**
     * city of User.
     */
    private String city;
    /**
    * One-to-one relationship with MasterWallet.
    */
    @OneToOne(mappedBy = "user")
    private MasterWallet masterWallet;
    /**
     * One-to-one relationship with MainWallet.
     */
    @OneToOne(mappedBy = "user")
    private MainWallet mainWallet;
/**
 * One-to-many relationship with Transaction history.
     */
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactionHistory;
    /**
     * FCM token for push notifications.
     */
    private String fcmToken;
    /**
     * One-to-many relationship with Reminder.
     */
    @OneToMany(mappedBy = "user")
    private List<Reminder> reminders;
    /**
     * Profile photo of the user.
     */

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] profilePhoto;

    /**
     * Hashed phone number for queries.
     */
    private String phoneNumberHash;

    /**
     * Indicates if the user account is blocked.
     */
    private Boolean isBlocked = false;

    /**
     * Indicates if the MPIN is created for the user.
     */
    @Transient
    private Boolean isMpinCreated;

    /**
     * Role of the user in the system.
     */
    @Enumerated(EnumType.STRING)
    private UserRoles userRole;

    /**
     * One-to-many relationship with GlobalPotMembers.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<GlobalPotMembers> globalPotMemberships;

    /**
     * Preferred landing page for the user.
     */
    private String preferredLandingPage;

}

