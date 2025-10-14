package com.bxb.sunduk_pay.request;
import com.bxb.sunduk_pay.model.UserContact;
import lombok.Data;
import java.util.List;

/**
 * Request object for uploading user contacts.
 */
@Data
public class ContactRequest {
    /** The ID of the user uploading the contacts. */
    private Long userId;
    /** The list of user contacts to be uploaded. */
    private List<UserContact> contacts;
}

