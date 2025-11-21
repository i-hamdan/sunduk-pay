package com.bxb.sunduk_pay.postgress.repository;

import com.bxb.sunduk_pay.postgress.model.PortfolioAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioAllocationRepository extends JpaRepository<PortfolioAllocation, Long> {
}
