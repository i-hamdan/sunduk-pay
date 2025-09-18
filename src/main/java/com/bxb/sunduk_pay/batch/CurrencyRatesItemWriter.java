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

/**
 * Spring Batch writer that merges all {@link CurrencyRates} in a chunk into a single document and persists it.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class CurrencyRatesItemWriter implements ItemWriter<CurrencyRates> {

    private final CurrencyRateRepository currencyRateRepository;

    @Override
    public void write(final Chunk<? extends CurrencyRates> chunk) {
        if (chunk == null || chunk.isEmpty()) {
            log.warn("Received null or empty chunk, nothing to write.");
            return;
        }

        log.info("Merging {} CurrencyRates items into a single document.", chunk.size());

        final Map<String, Double> allRates = new HashMap<>();
        for (CurrencyRates rate : chunk.getItems()) {
            if (rate != null && rate.getRates() != null) {
                allRates.putAll(rate.getRates());
            } else {
                log.warn("Skipping null CurrencyRates or rates map.");
            }
        }

        if (allRates.isEmpty()) {
            log.warn("No rates found in this chunk, skipping save.");
            return;
        }

        final CurrencyRates merged = new CurrencyRates();
        merged.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        merged.setRates(allRates);

        currencyRateRepository.save(merged);
        log.info("Saved merged rates to MongoDB. Total pairs: {}", allRates.size());
    }
}
