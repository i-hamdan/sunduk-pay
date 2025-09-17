package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.UserContact;
import lombok.Data;

import java.util.List;

/**
 * Represents a request payload containing user contacts.
 * <p>
 * Typically used when a user wants to sync or submit their contacts
 * to the system.
 * </p>
 */
@Data
public class ContactRequest {

    /**
     * The unique identifier of the user submitting the contacts.
     */
    private String userId;

    /**
     * A list of contacts associated with the user.
     */
    private List<UserContact> contacts;
}
