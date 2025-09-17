package com.bxb.sunduk_pay.response;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor

@Builder
public class UserResponse {
    private String uuid;
    private String fullName;
//    private String gender;
    private String email;
//    private String phoneNumber;
 private String message;
}
