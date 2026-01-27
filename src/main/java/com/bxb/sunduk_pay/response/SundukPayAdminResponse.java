package com.bxb.sunduk_pay.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO used to return the result of
 * admin-related operations in Sunduk Pay.
 * Contains a status indicator and a descriptive message.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SundukPayAdminResponse {

    /**
     * Message describing the outcome of the admin operation.
     */
    private String message;

    /**
     * Status of the operation.
     * Typically represents success or failure.
     */
    private String status;

    private String globalPotId;
}
