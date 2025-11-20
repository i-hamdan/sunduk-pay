package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentRepository extends
        JpaRepository<Investment, String> {

    Optional<Investment> findBySubWalletSubWalletId(
            String subWalletId);
}
