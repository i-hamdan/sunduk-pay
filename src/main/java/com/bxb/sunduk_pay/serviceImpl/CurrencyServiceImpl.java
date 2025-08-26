package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.CurrencyMapper;

import com.bxb.sunduk_pay.exception.CustomExchangeRateException;
import com.bxb.sunduk_pay.exception.InvalidCurrencyType;
import com.bxb.sunduk_pay.exception.NullAmountException;
import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.repository.CurrencyRateRepository;
import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyMapper mapper;
    private final CurrencyRateRepository currencyRateRepository;

    @Value("${exchange.api.url}")
    private String exchangeApiUrl;


    private final RestTemplate restTemplate;

    public CurrencyServiceImpl(CurrencyMapper mapper, CurrencyRateRepository currencyRateRepository, RestTemplate restTemplate) {
        this.currencyRateRepository = currencyRateRepository;
        log.debug("Initializing CurrencyServiceImpl with CurrencyMapper and RestTemplate");
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public CurrencyResponse convertCurrency(CurrencyRequest currencyRequest) {
        log.info("Currency conversion started for request: {}", currencyRequest);
        log.debug("Fetching exchange rate for {} to {}", currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());

        double exchangeRate = fetchExchangeRate(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
        log.debug("Exchange rate fetched successfully: {}", exchangeRate);

        if (currencyRequest.getAmount() == null) {
            log.error("Invalid amount received in request: {}", currencyRequest.getAmount());
            throw new NullAmountException("Amount Cannot Be null   " + currencyRequest.getAmount());
        }

        log.debug("Converting amount {} with exchange rate {}", currencyRequest.getAmount(), exchangeRate);
        double converted = currencyRequest.getAmount() * exchangeRate;
        log.debug("Converted amount = {}", converted);

        double fee = 0.10;
        log.debug("Applying fee deduction of {}", fee);

        double finalAmount = converted - fee;
        log.debug("Final amount after fee deduction = {}", finalAmount);
        log.debug("Fetching historical rates for {} to {}", currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());







        if (currencyRequest.getTimeSeries()== TimeSeries.WEEK) {
            List<CurrencyRatesResponse> weeklyRates = fetchWeekRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, null, null, weeklyRates);
        }
        else if (currencyRequest.getTimeSeries()==TimeSeries.MONTH) {
            List<CurrencyRatesResponse> monthlyRates = fetchMonthlyRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, null, monthlyRates, null);
        }
        else if (currencyRequest.getTimeSeries() ==TimeSeries.YEAR) {
            List<CurrencyRatesResponse> yearlyRates = fetchYearlyRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, yearlyRates, null, null);
        }
        else {
            throw new ResourceNotFoundException("please provide Time Series");
        }
    }

    private double fetchExchangeRate(String from, String to) {
        log.debug("Preparing to fetch exchange rate from API for {} to {}", from, to);
        String url = exchangeApiUrl + "/" + from;
        log.debug("Constructed API URL: {}", url);

        ResponseEntity<Map> response;
        try {
            log.debug("Calling external API for exchange rate...");
            response = restTemplate.getForEntity(url, Map.class);
            log.debug("API call successful, response received");
        } catch (Exception e) {
            log.error("API call failed for fromCurrency={} with error: {}", from, e.getMessage());
            throw new InvalidCurrencyType("Invalid currency: " + from);
        }

        Map<String, Object> body = response.getBody();
        log.debug("Parsing API response body: {}", body);

        if (body == null) {
            log.error("Response from exchange rate API is null!");
            throw new CustomExchangeRateException("Empty response from exchange rate API");
        }

        if (!body.containsKey("conversion_rates")) {
            log.error("Missing 'conversion_rates' in response from exchange api");
            throw new CustomExchangeRateException("Missing 'conversion_rates' in response from external api");
        }

        Map<String, Object> rates = (Map<String, Object>) body.get("conversion_rates");
        log.debug("Extracted conversion_rates: {}", rates);

        if (!rates.containsKey(to)) {
            log.error("Currency {} not found in conversion rates", to);
            throw new InvalidCurrencyType("Currency '" + to + "' not found in conversion rates");
        }

        double rate = Double.parseDouble(rates.get(to).toString());
        log.info("Exchange rate for {} to {}: {}", from, to, rate);
        return rate;
    }

    /**
     * Fetches historical rates
     */
    private List<CurrencyRatesResponse> fetchYearlyRates(String from, String to) {
        String currencyPair = from.concat(to);
        LocalDate oneYear = LocalDate.of(2024, 8, 19);

        log.info("Fetching yearly rates for currencyPair={} from date={}", currencyPair, oneYear);

        List<CurrencyRates> ratesForLastYear = currencyRateRepository.findSpecificRate(oneYear, currencyPair);

        log.debug("Yearly raw data fetched: {}", ratesForLastYear);

        List<CurrencyRatesResponse> response = mapper.toCurrencyRatesResponses(ratesForLastYear, currencyPair,TimeSeries.YEAR);

        log.info("Yearly rates mapped successfully. Count={}", response.size());

        return response;
    }


    private List<CurrencyRatesResponse> fetchMonthlyRates(String from, String to) {
        String currencyPair = from.concat(to);
        LocalDate oneMonth = LocalDate.of(2025, 7, 19);

        log.info("Fetching monthly rates for currencyPair={} from date={}", currencyPair, oneMonth);

        List<CurrencyRates> ratesForLastMonth = currencyRateRepository.findSpecificRate(oneMonth, currencyPair);

        log.debug("Monthly raw data fetched: {}", ratesForLastMonth);

        List<CurrencyRatesResponse> response = mapper.toCurrencyRatesResponses(ratesForLastMonth, currencyPair,TimeSeries.MONTH);

        log.info("Monthly rates mapped successfully. Count={}", response.size());

        return response;
    }



    private List<CurrencyRatesResponse> fetchWeekRates(String from, String to) {
        String currencyPair = from.concat(to);
        LocalDate oneWeek = LocalDate.of(2025, 8, 13);

        log.info("Fetching weekly rates for currencyPair={} from date={}", currencyPair, oneWeek);

        List<CurrencyRates> ratesForLastWeek = currencyRateRepository.findSpecificRate(oneWeek, currencyPair);

        log.debug("Weekly raw data fetched: {}", ratesForLastWeek);

        List<CurrencyRatesResponse> response = mapper.toCurrencyRatesResponses(ratesForLastWeek, currencyPair,TimeSeries.WEEK);

        log.info("Weekly rates mapped successfully. Count={}", response.size());

        return response;
    }
}