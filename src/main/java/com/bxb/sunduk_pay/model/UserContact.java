package com.bxb.sunduk_pay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user's contact information, including their
 * name and phone number.
 * <p>
 * This class is a simple model used for storing and transferring
 * user contact data within the application.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContact {

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The phone number of the user.
     */
    private String phone;
}
