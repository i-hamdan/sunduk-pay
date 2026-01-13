package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.AuthenticationSession;
import com.bxb.sunduk_pay.model.User;

/**
 * Service interface for managing authentication sessions.
 * Defines operations related to creating and maintaining
 * user authentication session data.
 */
public interface AuthenticationSessionService {

    /**
     * Saves an authentication session for a user.
     *
     * @param jSessionId the unique session identifier created during authentication
     * @param user the authenticated user for whom the session is being created
     */
    void saveSession(String jSessionId, User user);

    AuthenticationSession validateSession(String jSessionId);
}
