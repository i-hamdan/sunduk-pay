package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Mpin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Repository interface for managing MPIN entities.
 * Extends JpaRepository to provide CRUD operations.
 */

public interface MpinRepository extends JpaRepository<Mpin,Long> {
    /**
     * Finds an MPIN by the associated user's UUID.
     * @param uuid the UUID of the user
     * @return an Optional containing the MPIN if found,
     * or empty if not found
     */
    Optional<Mpin> findByUser_Uuid(String uuid);

}
