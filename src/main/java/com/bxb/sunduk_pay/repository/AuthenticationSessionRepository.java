//package com.bxb.sunduk_pay.repository;
//
//import com.bxb.sunduk_pay.model.AuthenticationSession;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
///**
// * Repository interface for managing {@link AuthenticationSession} entities.
// * Provides CRUD operations and database access methods
// * for authentication session persistence.
// */
//public interface AuthenticationSessionRepository
//        extends JpaRepository<AuthenticationSession, String> {
//
//    /**
//     * Finds an active authentication session by its JSessionId.
//     *
//     * @param jSessionId the JSessionId of the authentication session
//     * @return an Optional containing the found AuthenticationSession,
//     *         or empty if no active session is found
//     */
//    Optional<AuthenticationSession> findByjSessionIdAndIsActiveSessionTrue(
//            String jSessionId);
//}
