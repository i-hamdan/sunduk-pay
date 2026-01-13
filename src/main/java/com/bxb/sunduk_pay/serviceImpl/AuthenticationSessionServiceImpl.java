package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.model.AuthenticationSession;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.AuthenticationSessionRepository;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of {@link AuthenticationSessionService}.
 * Responsible for creating and persisting authentication
 * session details for authenticated users.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationSessionServiceImpl implements AuthenticationSessionService {

    /**
     * Repository used for persisting authentication session data.
     */
    private final AuthenticationSessionRepository authenticationSessionRepository;

    /**
     * Session validity duration in minutes.
     * This value is used to calculate the session expiration time.
     */
    public final long session = 30;

    /**
     * Creates and saves an authentication session for the given user.
     * The session includes session ID, user UUID, user role,
     * creation timestamp, expiration timestamp, and active status.
     *
     * @param jSessionId the unique session identifier generated during authentication
     * @param user the authenticated user for whom the session is created
     */
    @Override
    public void saveSession(String jSessionId, User user) {

        AuthenticationSession authenticationSession = AuthenticationSession.builder()
                .jSessionId(jSessionId)
                .userUuid(user.getUuid())
                .userName(user.getFullName())
                .roles(user.getUserRole())
                .isActiveSession(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(session))
                .build();

        authenticationSessionRepository.save(authenticationSession);
    }
}
