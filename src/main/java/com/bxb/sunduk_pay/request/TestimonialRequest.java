package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class TestimonialRequest {
    private String details;
    private String authorName;
    private String profession;
    private byte[] profileImage;
    private Boolean isSundukAdmin;
}