package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.UserRoles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * Entity representing an authentication session for a user.
 * This class stores session-related information such as
 * session ID, user UUID, user role, creation time, expiration time,
 * and session status.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationSession {

    /**
     * Represents the JSESSIONID created during user authentication.
     */
    private String jSessionId;

    /**
     * Unique UUID of the user associated with this session.
     */
    private String userUuid;

    /**
     * User name associated with this session.
     */
    private String userName;

    /**
     * Role assigned to the user for this session.
     * The value is persisted as a string.
     */
    @Enumerated(EnumType.STRING)
    private UserRoles roles;

}
