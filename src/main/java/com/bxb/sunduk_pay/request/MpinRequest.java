package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.MpinRequestType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MpinRequest {

    private String uuid;
    private String mpin;
    private String newMpin;
    private String email;
    private String otp;
    private MpinRequestType mpinRequestType;
}
