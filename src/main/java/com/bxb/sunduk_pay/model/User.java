package com.bxb.sunduk_pay.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a user in the system.
 */
@Document
@Data
public class User {
    /**
     * Unique identifier for the user.
     */
    @Id
    private String uuid;

    /**
     * The user's full name.
     */
    private String fullName;

    /**
     * The user's gender.
     */
    private String gender;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's phone number.
     */
    private String phoneNumber;

    /**
     * The user's password.
     */
    private String password;

    /**
     * Indicates if the user is deleted.
     */
    private Boolean isDeleted;

    /**
     * Reference to the user's master wallet.
     */
    @DBRef
    private MasterWallet masterWallet;

    /**
     * Reference to the user's main wallet.
     */
    @DBRef
    private MainWallet mainWallet;

    /**
     * List of contacts associated with the user.
     */
    private List<UserContact> contacts;

}
