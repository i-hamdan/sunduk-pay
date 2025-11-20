package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.StockUnitPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockUnitPriceRepository extends JpaRepository<StockUnitPrice, Long> {


    Optional<StockUnitPrice> findByDate(LocalDate date);

}
