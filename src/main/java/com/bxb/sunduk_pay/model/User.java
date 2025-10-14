package com.bxb.sunduk_pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * Represents a user in the system.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;
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
     * One-to-many relationship with UserContact.
     */
    @OneToMany(mappedBy = "user")
    private List<UserContact> contacts;
}

