package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing UserContact entities.
 */
public interface ContactRepository extends JpaRepository<UserContact, Long> {

    /**
     * Finds a UserContact by phone number.
     * @param phone the phone number to search for
     * @return an Optional containing the UserContact if found,
     * or empty if not found
     */
    Optional<UserContact> findByPhone(String phone);
}
