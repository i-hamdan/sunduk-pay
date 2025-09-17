package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
@Component
@Log4j2
public class CurrencyRatesItemProcessor implements ItemProcessor<CurrencyPair, CurrencyRates> {

    private final RestTemplate restTemplate = new RestTemplate();

        @Override
        public CurrencyRates process(CurrencyPair pair) throws Exception {
            String from = pair.name().substring(0, 3);
            String to   = pair.name().substring(3);
            log.info("Processing CurrencyPair: {} -> {}", from, to);

            String url = String.format(
                    "https://v6.exchangerate-api.com/v6/136cca7e5f6ec25648bc5eca/pair/%s/%s",
                    from, to
            );
            log.debug("Calling API URL: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

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
