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

    @Id
    private String uuid;

    /**
     * Full name of the user.
     */
    private String fullName;

    // Optional: You can uncomment if you want firstName/lastName separately
    // private String firstName;
    // private String lastName;

    private String gender;
    private String email;
    private String phoneNumber;

    /**
     * User's password (should be stored hashed in production).
     */
    private String password;

    /**
     * Indicates whether the user is deleted or inactive.
     */
    private Boolean isDeleted;

    @DBRef
    private MasterWallet masterWallet;

    @DBRef
    private MainWallet mainWallet;

    /**
     * List of user contacts.
     */
    private List<UserContact> contacts;
}
