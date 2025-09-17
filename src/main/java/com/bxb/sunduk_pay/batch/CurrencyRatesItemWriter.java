package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Log4j2
public class CurrencyRatesItemWriter implements ItemWriter<CurrencyRates> {

    private final CurrencyRateRepository currencyRateRepository;
    @Override
    public void write(Chunk<? extends CurrencyRates> chunk) throws Exception {
        Map<String, Double> allRates = new HashMap<>();

        CurrencyRates merged = new CurrencyRates();

        merged.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));

        log.info("Merging {} ExchangeRate items into a single document.", chunk.size());

        for (CurrencyRates rate : chunk.getItems()) {
            allRates.putAll(rate.getRates());
        }

        merged.setRates(allRates);
        currencyRateRepository.save(merged); // ek hi document save hoga
        log.info(" Saved merged  rates to MongoDB. Total pairs: {}", allRates.size());

    }
}
