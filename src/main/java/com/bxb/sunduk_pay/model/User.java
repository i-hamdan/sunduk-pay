package com.bxb.sunduk_pay.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a user in the system.
 * Stores personal details, associated wallets, and contacts.
 */
@Document
@Data
public class User {

    /** Unique identifier for the user. */
    @Id
    private String uuid;

    /** Full name of the user. */
    private String fullName;

    /** Gender of the user (e.g., Male, Female, Other). */
    private String gender;

    /** Email address of the user. */
    private String email;

    /** Phone number of the user. */
    private String phoneNumber;

    /** User's password (should be stored hashed in production). */
    private String password;

    /** Indicates whether the user is deleted or inactive. */
    private Boolean isDeleted;

    /** Reference to the user's master wallet. */
    @DBRef
    private MasterWallet masterWallet;

    /** Reference to the user's main wallet. */
    @DBRef
    private MainWallet mainWallet;

    /** List of contacts associated with the user. */
    private List<UserContact> contacts;
}
