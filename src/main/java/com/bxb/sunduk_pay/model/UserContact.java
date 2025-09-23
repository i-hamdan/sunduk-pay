package com.bxb.sunduk_pay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a user's contact information.
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
