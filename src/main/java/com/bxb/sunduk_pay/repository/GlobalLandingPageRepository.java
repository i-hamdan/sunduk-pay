package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalLandingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing GlobalLandingPage entities in the database.
 */
@Repository
public interface GlobalLandingPageRepository
        extends JpaRepository<GlobalLandingPage, Long> {

    /**
     * Find a GlobalLandingPage by its unique key.
     *
     * @param key the unique key of the landing page
     * @return an Optional containing the found GlobalLandingPage,
     * or empty if not found
     */
    Optional<GlobalLandingPage> findByKey(String key);
}
