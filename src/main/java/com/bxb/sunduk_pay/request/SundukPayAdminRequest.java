package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.AdminRequestType;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.PotStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

}
