package com.bxb.sunduk_pay.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/** * Response object for photo upload operations.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResponse {
    /** URL of the uploaded photo. */
    private String message;
}
