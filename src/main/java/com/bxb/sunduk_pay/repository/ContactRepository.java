package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing UserContact entities.
 */
public interface ContactRepository extends JpaRepository<UserContact, Long> {
    Optional<UserContact> findByPhone(String phone);
}
