package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class InvestmentSchedulerTest {

    @Autowired
    StockUnitPriceRepository stockUnitPriceRepository;

    @Test
    void run(){
        Optional<StockUnitPrice> latest = stockUnitPriceRepository.findLatest();
        log.info("Latest Stock Unit Price: {}", latest);

    }
}