/**
 * Contains request DTOs for the Sunduk Pay application.
 *
 * These classes represent incoming payloads for various API
 * endpoints. They ensure smooth data transfer between client
 * and server layers.
 */
package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.UserContact;
import lombok.Data;

import java.util.List;

/**
 * Request payload containing user contacts.
 * Used when a user wants to sync or submit
 * their contacts to the system.
 */
@Data
public class ContactRequest {

    /**
     * The unique ID of the user submitting
     * the contacts.
     */
    private String userId;

    /**
     * List of contacts linked to the user.
     */
    private List<UserContact> contacts;
}
