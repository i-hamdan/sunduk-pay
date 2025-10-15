package com.bxb.sunduk_pay.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for user-related operations.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    /** The unique identifier of the user. */
    private String uuid;
    /** The full name of the user. */
    private String fullName;
    /** The email address of the user. */
    private String email;
    /** message regarding the user operation status. */
    private String message;
}

