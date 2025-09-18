package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

/**
 * Spring Batch processor that fetches the conversion rate for a {@link CurrencyPair}
 * from an external API and builds a {@link CurrencyRates} entity.
 */
@Component
@Log4j2
public class CurrencyRatesItemProcessor implements ItemProcessor<CurrencyPair, CurrencyRates> {

    /**
     * In production you’d typically inject this as a @Bean for reuse and testability.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public CurrencyRates process(final CurrencyPair pair) {
        if (pair == null) {
            log.warn("Received null CurrencyPair to process.");
            return null;
        }

        final String from = pair.name().substring(0, 3);
        final String to = pair.name().substring(3);
        log.info("Processing CurrencyPair: {} -> {}", from, to);

        final String url = String.format(
                "https://v6.exchangerate-api.com/v6/136cca7e5f6ec25648bc5eca/pair/%s/%s",
                from, to
        );
        log.debug("Calling API URL: {}", url);

        final Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (Objects.isNull(response)) {
            log.error("API response was null for pair {} -> {}", from, to);
            return null; // returning null skips the item in Spring Batch
        }

        final Object rateObj = response.get("conversion_rate");
        if (!(rateObj instanceof Number)) {
            log.error("Conversion rate missing or invalid for {} -> {}: {}", from, to, rateObj);
            return null;
        }

        final double rate = ((Number) rateObj).doubleValue();
        log.info("Received conversion rate for {} -> {}: {}", from, to, rate);

        final CurrencyRates currencyRates = new CurrencyRates();
        currencyRates.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        currencyRates.setRates(Map.of(pair.name(), rate));

        log.debug("Built CurrencyRates object: {}", currencyRates);
        return currencyRates;
    }
}
