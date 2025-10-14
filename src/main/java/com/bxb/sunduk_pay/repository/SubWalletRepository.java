package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.SubWallet;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository interface for managing SubWallet
 * entities in the database.
 */
public interface SubWalletRepository extends
        JpaRepository<SubWallet,Long> {
}
