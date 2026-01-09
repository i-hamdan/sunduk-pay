package com.bxb.sunduk_pay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an anonymous user's
 * identity in a group chat.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousIdentityDTO {
    /** Unique identifier for the anonymous user. */
    private String anonymousId;
    /** Color associated with the anonymous user. */
    private String anonymousColor;
}
