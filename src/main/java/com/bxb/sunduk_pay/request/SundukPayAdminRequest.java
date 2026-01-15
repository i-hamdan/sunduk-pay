package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.AdminRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Request DTO used by admin users to perform operations
 * related to Sunduk Pay global pots.
 * This request supports pot management, contributor handling,
 * follower management, document uploads, and pagination.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SundukPayAdminRequest {

    /**
     * Unique UUID of the admin performing the operation.
     */
    private String adminUuid;

    /**
     * Type of admin request being performed.
     */
    private AdminRequestType adminRequestType;

    /**
     * Unique identifier of the global pot.
     */
    private String globalPotId;

    /**
     * Document ID for global pot
     */
    private String globalPotDocumentId;

}
