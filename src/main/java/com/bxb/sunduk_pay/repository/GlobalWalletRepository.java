package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalWallet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing GlobalWallet entities in the database.
 */
public interface GlobalWalletRepository extends JpaRepository<GlobalWallet, String> {
}
