package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class ContributorRequest {
    private String name;
    private Double amountContributed;
    private Boolean isAnonymous;
    private Boolean isUser;
    private byte[] profileImage;
}