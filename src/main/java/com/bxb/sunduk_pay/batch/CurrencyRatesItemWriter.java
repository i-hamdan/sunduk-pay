package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
//import com.bxb.sunduk_pay.repository.CustomCurrencyRateRepositoryImpl;
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
 * ItemWriter implementation that merges multiple CurrencyRates
 * items into a single document and saves it to MongoDB.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class CurrencyRatesItemWriter implements ItemWriter<CurrencyRates> {
    /** Repository for saving CurrencyRates to MongoDB. */
//    private final CustomCurrencyRateRepositoryImpl customCurrencyRateRepositoryImpl;

    /**
     * Writes a chunk of CurrencyRates items by merging them into a
     * single document and saving it to MongoDB.
     * @param chunk The chunk of CurrencyRates items to write.
     * @throws Exception If an error occurs during writing.
     */
    @Override
    public void write(
            final Chunk<? extends CurrencyRates> chunk
            ) throws Exception {
        Map<String, Double> allRates = new HashMap<>();
        CurrencyRates merged = new CurrencyRates();
        merged.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        log.info(
                "Merging {} ExchangeRate items into a single document.",
                chunk.size()
        );
        for (CurrencyRates rate : chunk.getItems()) {
        //    allRates.putAll(rate.getRates());
        }
        //merged.setRates(allRates);
       // currencyRateRepository.save(merged); // ek hi document save hoga
        log.info(
                "Saved merged rates to MongoDB. Total pairs: {}",
                allRates.size()
        );
    }
}
