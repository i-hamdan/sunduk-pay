package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing basic user information.
 * <p>
 * Can be used for user details retrieval or API responses after operations
 * like registration or profile updates.
 * </p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    /** Unique identifier of the user. */
    private String uuid;

    /** Full name of the user. */
    private String fullName;

    /** Email address of the user. */
    private String email;

    /** Optional message related to the user operation. */
    private String message;
}
