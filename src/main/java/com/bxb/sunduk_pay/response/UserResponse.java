package com.bxb.sunduk_pay.response;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor

@Builder
/**
 * Response object for user-related operations.
 */
public class UserResponse {
    private String uuid;
    private String fullName;
    private String email;
    private String message;
}
