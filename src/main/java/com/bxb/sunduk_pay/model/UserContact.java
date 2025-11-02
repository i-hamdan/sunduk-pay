package com.bxb.sunduk_pay.model;

import com.ctc.wstx.evt.WstxEventReader;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a user's contact information.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
