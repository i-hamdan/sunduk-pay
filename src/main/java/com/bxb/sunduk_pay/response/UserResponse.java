package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Response object for user login containing user details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    /** The unique identifier of the user. */
    private String uuid;
    /** The email address of the user. */
    private String email;
    /** The full name of the user. */
    private String fullName;
    /** The phone number of the user. */
    private String phoneNumber;
    /** message regarding the user operation status. */
    private String message;
    /** present address of user*/
    private String presentAddress;
    /**permanent address of user*/
    private String permanentAddress;

    private String dob;

    private String profilePhoto;
}














