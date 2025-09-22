package com.bxb.sunduk_pay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Model representing a user's contact information.
 */
public class UserContact {

    private String name;
    private String phone;
}
