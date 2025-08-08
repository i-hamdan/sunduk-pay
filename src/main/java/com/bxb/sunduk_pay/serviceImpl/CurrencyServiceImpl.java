package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.CurrencyMapper;
import com.bxb.sunduk_pay.exception.CustomExchangeRateException;
import com.bxb.sunduk_pay.exception.InvalidCurrencyType;
import com.bxb.sunduk_pay.exception.NullAmountException;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@Log4j2
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyMapper mapper;
    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    private final RestTemplate restTemplate;

    public CurrencyServiceImpl(CurrencyMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public CurrencyResponse convertCurrency(String from, String to, Double amount) {

        double exchangeRate = fetchExchangeRate(from, to);
        log.debug("Currency conversion requested: from = {}, to = {}", from, to);

        if (amount==null||amount==0.000){
            throw new NullAmountException("Amount Cannot Be null   " + amount);
        }

        double converted = amount * exchangeRate;
        log.debug("Amount after applying exchange rate: {}", converted);
        double fee = 0.10;
        double finalAmount = converted - fee;
        log.debug("final amount after fee deduction = {}", finalAmount);
        log.info("Currency conversion response generated successfully");
        return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount);

    }

    private double fetchExchangeRate(String from, String to) {

        String url = exchangeApiUrl + "/" + from;

        ResponseEntity<Map> response;
        try {
            response = restTemplate.getForEntity(url, Map.class);
        } catch (Exception e) {
            log.error("API call failed for fromCurrency={}", from);
            throw new InvalidCurrencyType("Invalid currency: " + from);
        }


        Map<String, Object> body = response.getBody();


        if (response.getBody() == null) {
            log.error("Response from exchange rate API is null!");
            throw new CustomExchangeRateException("Empty response from exchange rate API");
        }


        if (!body.containsKey("conversion_rates")) {
            log.error("Missing 'conversion_rates' in response from exchange api");
            throw new CustomExchangeRateException("Missing 'conversion_rates' in response from external api");
        }
        Map<String, Object> rates = (Map<String, Object>) body.get("conversion_rates");

        if (!rates.containsKey(to)) {
            log.error("Currency {} not found in conversion rates", to);
            throw new InvalidCurrencyType("Currency '" + to + "' not found in conversion rates");
        }
        double rate = Double.parseDouble(rates.get(to).toString());
        log.debug("Exchange rate for {} to {}: {}", from, to, rate);
        return rate;

    }

}

