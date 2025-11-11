package com.bxb.sunduk_pay.model;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
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
    private String  uuid;
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
//    /**
//     * One-to-many relationship with UserContact.
//     */
//    @OneToMany(mappedBy = "user")
//    private List<UserContact> contacts;
    /**
     * Indicates if the MPIN is created for the user.
     */
    @Transient
    private Boolean isMpinCreated;

}

