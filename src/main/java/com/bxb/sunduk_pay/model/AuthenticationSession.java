package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing an authentication session for a user.
 * This class stores session-related information such as
 * session ID, user UUID, user role, creation time, expiration time,
 * and session status.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authentication_sessions")
public class AuthenticationSession {

    /**
     * Unique identifier for the authentication session.
     * Represents the JSESSIONID created during user authentication.
     */
    @Id
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

    /**
     * Date and time when the session was created.
     */
    private LocalDateTime createdAt;

    /**
     * Date and time when the session will expire.
     */
    private LocalDateTime expiresAt;

    /**
     * Indicates whether the session is currently active.
     * True if the session is valid, false if expired or invalidated.
     */
    private boolean isActiveSession;
}
