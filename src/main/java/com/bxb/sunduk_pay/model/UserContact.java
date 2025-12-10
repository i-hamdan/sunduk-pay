package com.bxb.sunduk_pay.model;


import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Model representing a user's contact information.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_contacts")
public class UserContact {
    /**
     * Unique identifier for the contact.
     */
    @Id
    private String contactId;
    /**
     * The name of the user.
     */
    private String name;

    /**
     * The phone number of the user.
     */
    private String phone;

    /**
     * The UPI ID of the recipient.
     */
    private String upiId;
}
