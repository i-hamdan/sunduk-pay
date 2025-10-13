package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

/**
 * ItemProcessor implementation for processing CurrencyPair items
 * and fetching their corresponding exchange rates from an external API.
 */
@Component
@Log4j2
public class CurrencyRatesItemProcessor implements
        ItemProcessor<CurrencyPair, CurrencyRates> {

    /** Length of a currency code (e.g., "USD" is 3 characters). */
    private static final int CURRENCY_CODE_LENGTH = 3;

    /** Base URL for the exchange rate API,
     * injected from application properties. */
    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    /** RestTemplate for making HTTP requests to the exchange rate API. */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Processes a CurrencyPair to fetch its exchange rate and
     * constructs a CurrencyRates object.
     *
     * @param pair the CurrencyPair to process
     * @return a CurrencyRates object containing the exchange rate
     * @throws Exception if an error occurs during processing.
     */
    @Override
    public CurrencyRates process(final CurrencyPair pair) throws Exception {
        String from = pair.name().substring(0, CURRENCY_CODE_LENGTH);
        String to = pair.name().substring(CURRENCY_CODE_LENGTH);
        log.info("Processing CurrencyPair: {} -> {}", from, to);

        String url = exchangeApiUrl
                + "/pair"
                + "/" + from
                + "/" + to;
        log.debug("Calling API URL: {}", url);

        Map<String, Object> response = restTemplate.
                getForObject(url, Map.class);

        if (response == null) {
            log.error("API response was null for pair {} -> {}", from, to);
            return null;
        }
        Double rate = (Double) response.get("conversion_rate");

        log.info("Received conversion rate for {} -> {}: {}", from, to, rate);
        CurrencyRates currencyRates = new CurrencyRates();
        currencyRates.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        currencyRates.setRates(Map.of(pair.name(), rate));
        log.debug("Built ExchangeRate object: {}", currencyRates);
        return currencyRates;
    }
}
