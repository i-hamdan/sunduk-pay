package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.AuthenticationSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link AuthenticationSession} entities.
 * Provides CRUD operations and database access methods
 * for authentication session persistence.
 */
public interface AuthenticationSessionRepository
        extends JpaRepository<AuthenticationSession, String> {

}
