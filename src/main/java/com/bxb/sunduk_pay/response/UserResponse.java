package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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
    /** Address of user.*/
    private String address;
    /** date of birth of user.*/
    private String dob;
    /** profile photo of user.*/
    private String profilePhoto;
    /** flag indicating if the user exists.*/
    private boolean userExists;
    /** flag indicating if the user is verified. */
    private boolean isVerified;
    /** flag indicating if the user is a Google user. */
    private boolean isGoogleUser;
}















