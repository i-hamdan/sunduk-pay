package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.AutoPayConfirmation;
import com.bxb.sunduk_pay.util.ConfirmationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** * Repository interface for managing AutoPayConfirmation entities.
 * Provides CRUD operations and database interactions for auto-payment
 * confirmations.
 */
@Repository
public interface AutoPayConfirmationRepository
        extends JpaRepository<AutoPayConfirmation,String> {

    /**
     * Finds an AutoPayConfirmation by reminder ID and confirmation status.
     *
     * @param reminderId the ID of the reminder
     * @param status     the confirmation status to filter by
     * @return an Optional containing the found AutoPayConfirmation,
     * or empty if not found
     */
    Optional<AutoPayConfirmation> findByReminderReminderIdAndStatus(
            String reminderId,
            ConfirmationStatus status
    );

    /**
     * Checks if an AutoPayConfirmation exists for a given reminder ID
     * and confirmation status.
     * @param reminderId the ID of the reminder
     * @param status     the confirmation status to filter by
     * @return true if an AutoPayConfirmation exists, false otherwise
     */
    boolean existsByReminderReminderIdAndStatus(
            String reminderId,
            ConfirmationStatus status);
}
