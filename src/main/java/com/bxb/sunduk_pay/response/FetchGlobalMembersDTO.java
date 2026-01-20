package com.bxb.sunduk_pay.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Data Transfer Object (DTO) for fetching global pot members.
 * This class encapsulates the essential information
 * about members associated with a global pot,
 * including their identity, role, location,
 * profile image, and contribution details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchGlobalMembersDTO {

    /** Unique identifier for the user. */
    private String uuid;
    /** Full name of the user. */
    private String fullName;
    /** Role of the user in the global pot. */
    private String role;
    /** Location of the user. */
    private String location;
    /** Profile image URL of the user. */
    private String profileImageUrl;
    /** Amount contributed by the user to the global pot. */
    private Double contributedAmount;
}
