package com.bxb.sunduk_pay.model;

import com.ctc.wstx.evt.WstxEventReader;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a user's contact information.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContact {
    /**
     * Unique identifier for the contact.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
 * The email address of the user.
 */
    private String email;
    /**
     * The user associated with this contact information.
 */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
