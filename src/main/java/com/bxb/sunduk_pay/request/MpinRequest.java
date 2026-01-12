package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.MpinRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MpinRequest {

    /*** UUID of the user. */
    private String uuid;
    /**
     * Current MPIN of the user.
     */
    private String mpin;
    /**
     * New MPIN to be set or updated.
     */
    private String newMpin;
    /**
     * Email associated with the MPIN request.
     */
    private String email;
    /**
     * One-time password for verification.
     */
    private String otp;
    /**
     * Type of MPIN request (e.g., SET, RESET, CHANGE).
     */
    private MpinRequestType mpinRequestType;
}
