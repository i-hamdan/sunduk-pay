package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.model.UserContact;
import lombok.Data;

import java.util.List;
@Data
/**
 * Request object for uploading user contacts.
 */
public class ContactRequest {
    private String userId;
    private List<UserContact> contacts;
}
