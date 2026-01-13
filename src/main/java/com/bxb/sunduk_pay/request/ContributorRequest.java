package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class ContributorRequest {
    /**
     * Name of the contributor.
     */
    private String name;
    /**
     * Amount contributed by the contributor.
     */
    private Double amountContributed;
    /**
     * Indicates if the contributor is anonymous.
     */
    private Boolean isAnonymous;
    /**
     * Indicates if the contributor is a registered user.
     */
    private Boolean isUser;
    /**
     * Profile image as a byte array.
     */
    private byte[] profileImage;
}
