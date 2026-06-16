package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class TestimonialRequest {
    /**
     * Details of the testimonial.
     */
    private String details;
    /**
     * Name of the testimonial author.
     */
    private String authorName;
    /**
     * Profession of the testimonial author.
     */
    private String profession;
    /**
     * Profile image in byte array format.
     */
    private byte[] profileImage;
    /**
     * Indicates if the testimonial is for Sunduk admin.
     */
    private Boolean isSundukAdmin;
}
