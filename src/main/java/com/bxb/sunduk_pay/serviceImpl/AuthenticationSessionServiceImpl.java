package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.InvalidSessionException;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.model.AuthenticationSession;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.service.AuthenticationSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of {@link AuthenticationSessionService}.
 * Responsible for creating and persisting authentication
 * session details for authenticated users.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationSessionServiceImpl implements
        AuthenticationSessionService {

    private final RedisTemplate<String, AuthenticationSession> redisTemplate;

    /**
     * Session validity duration in minutes.
     */
    public final long session = 30;

    /**
     * Saves an authentication session for a user.
     * @param jSessionId the unique session identifier created during authentication
     * @param user the authenticated user for whom the session is being created
     */
    @Override
    public void saveSession(String jSessionId, User user) {

        log.info(
                "Creating session in Redis. sessionId="
                        + jSessionId
                        + ", role="
                        + user.getUserRole()
        );

        try {
            String sessionKey = jSessionId;

            AuthenticationSession authenticationSession = AuthenticationSession
                    .builder().jSessionId(jSessionId)
                    .userUuid(user.getUuid())
                    .userName(user.getFullName())
                    .roles(user.getUserRole())
                    .build();

            redisTemplate.opsForValue().set(sessionKey, authenticationSession);
            redisTemplate.expire(sessionKey, Duration.ofMinutes(session));

            log.info(
                    "Session stored in Redis successfully. sessionId= "
                            + jSessionId
                            + ", expiresIn= "
                            + session
                            + " minutes"
            );

        } catch (Exception ex) {
            log.error(
                    "Error while saving session for sessionId= "
                            + jSessionId
                            + ", error= "
                            + ex.getMessage(),
                    ex
            );
            throw new RedisOperationException(ex.getMessage());
        }
    }

    /**
     * Validates an existing session by its ID.
     * @param jSessionId
     * @return  authentication session details
     */
    @Override
    public AuthenticationSession validateSession(String jSessionId) {

        log.debug(
                "Validating session with sessionId= "
                        + jSessionId
        );

        AuthenticationSession authenticationSession =
                redisTemplate.opsForValue().get(jSessionId);

        if (authenticationSession == null) {
            log.warn(
                    "Invalid session. Session not found in Redis. sessionId= "
                            + jSessionId
            );
            throw new InvalidSessionException("Session not found or expired.");
        }

        return authenticationSession;
    }
}
