package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * this class is used to create mpin response
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MpinResponse {
    /** using for passing message response*/
    private String message;
    /** using for passing title respnse*/
    private String title;
}
