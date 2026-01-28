package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotInteraction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.CaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlobalPotInteractionRepository
        extends JpaRepository<GlobalPotInteraction, Long> {

    /**
     * Find interaction for a user and a pot
     * (used to UPDATE visitCount / timeSpent)
     */
    Optional<GlobalPotInteraction>
    findByUuidAndGlobalPot(User user, GlobalPot globalPot);

    /**
     * Fetch all interactions of a user
     * (used for recommendation scoring)
     */
    List<GlobalPotInteraction> findByUuid(User user);

    /**
     * Fetch interactions by category
     * (used for category preference)
     */
    List<GlobalPotInteraction>
    findByUuidAndCaseCategory(User user, CaseCategory caseCategory);
}
