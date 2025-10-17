package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
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
private final CurrencyRepository currencyRepository;
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


        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        log.info("Merging {} currency rate items for date {}",
                chunk.size(), today);

        CurrencyRates entity = new CurrencyRates();
        entity.setDate(today);

        for (CurrencyRates items : chunk.getItems()) {
            Map<String,Double> rates = items.getRates();
            if (rates==null) continue;
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                String pair = entry.getKey().toLowerCase();
                Double value = entry.getValue();
                try {
                    Field field = CurrencyRates
                            .class.getDeclaredField(pair);
                    field.setAccessible(true);
                    field.set(entity, value);
                    log.debug("Set field {} = {}",
                            pair, value);
                } catch (NoSuchFieldException e) {
                    log.debug("No matching column for currency pair: {}",
                            pair);

                }

            }
        }


        currencyRepository.save(entity);
        log.info(
                " {} with pairs.", today);
    }
}
