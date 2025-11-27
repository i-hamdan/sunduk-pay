package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Investment entities in the database.
 */
@Repository
public interface InvestmentRepository extends
        JpaRepository<Investment, String> {

    /**
     * Finds an active investment by its associated sub-wallet ID.
     *
     * @param subWalletId the ID of the sub-wallet
     * @return an Optional containing the active Investment if found,
     * otherwise empty
     */
    Optional<Investment> findBySubWalletSubWalletIdAndIsActiveTrue(
            String subWalletId);

    List<Investment> findByIsActiveTrue();

    /**
     * Finds all investments associated with a user's UUID.
     *
     * @param uuid the UUID of the user
     * @return a list of Investments associated with the user
     */
    List<Investment>findByUserUuid(String uuid);

}
