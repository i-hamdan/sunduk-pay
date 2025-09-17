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

/**
 * Service implementation for currency conversion and fetching historical exchange rates.
 * Handles real-time conversion using external API and historical data from the database.
 */
@Service
@Log4j2
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyMapper mapper;
    private final CurrencyRateRepository currencyRateRepository;

    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    private final RestTemplate restTemplate;

    /**
     * Constructs the CurrencyServiceImpl.
     *
     * @param mapper                 mapper to convert entity to response DTO
     * @param currencyRateRepository repository for currency rate data
     * @param restTemplate           RestTemplate for external API calls
     */
    public CurrencyServiceImpl(CurrencyMapper mapper,
                               CurrencyRateRepository currencyRateRepository,
                               RestTemplate restTemplate) {
        this.currencyRateRepository = currencyRateRepository;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        log.debug("Initializing CurrencyServiceImpl with CurrencyMapper and RestTemplate");
    }

    /**
     * Converts an amount from one currency to another and optionally fetches historical rates.
     *
     * @param currencyRequest request containing source currency, target currency, amount, and time series
     * @return CurrencyResponse containing converted amount, exchange rate, fees, and historical rates
     * @throws NullAmountException       if amount is null
     * @throws InvalidCurrencyType       if the currency is invalid
     * @throws CustomExchangeRateException if API response is invalid
     */
    @Override
    public CurrencyResponse convertCurrency(CurrencyRequest currencyRequest) {
        double exchangeRate = fetchExchangeRate(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());

        if (currencyRequest.getAmount() == null) {
            throw new NullAmountException("Amount cannot be null");
        }

        double converted = currencyRequest.getAmount() * exchangeRate;
        double fee = 0.10;
        double finalAmount = converted - fee;

        if (currencyRequest.getTimeSeries() == TimeSeries.WEEK) {
            List<CurrencyRatesResponse> weeklyRates = fetchWeekRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, null, null, weeklyRates);
        } else if (currencyRequest.getTimeSeries() == TimeSeries.MONTH) {
            List<CurrencyRatesResponse> monthlyRates = fetchMonthlyRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, null, monthlyRates, null);
        } else if (currencyRequest.getTimeSeries() == TimeSeries.YEAR) {
            List<CurrencyRatesResponse> yearlyRates = fetchYearlyRates(currencyRequest.getFromCurrency(), currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate, converted, fee, finalAmount, yearlyRates, null, null);
        } else {
            throw new ResourceNotFoundException("Please provide a valid Time Series");
        }
    }

    /**
     * Fetches the real-time exchange rate from external API.
     *
     * @param from source currency
     * @param to   target currency
     * @return exchange rate
     */
    private double fetchExchangeRate(String from, String to) {
        String url = exchangeApiUrl + "/" + from;
        ResponseEntity<Map> response;
        try {
            response = restTemplate.getForEntity(url, Map.class);
        } catch (Exception e) {
            throw new InvalidCurrencyType("Invalid currency: " + from);
        }

        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("conversion_rates")) {
            throw new CustomExchangeRateException("Invalid API response");
        }

        Map<String, Object> rates = (Map<String, Object>) body.get("conversion_rates");
        if (!rates.containsKey(to)) {
            throw new InvalidCurrencyType("Currency '" + to + "' not found in conversion rates");
        }

        return Double.parseDouble(rates.get(to).toString());
    }

    /** Fetches historical yearly rates. */
    private List<CurrencyRatesResponse> fetchYearlyRates(String from, String to) {
        LocalDate oneYear = LocalDate.of(2024, 8, 19);
        LocalDate endDate = LocalDate.of(2025, 8, 19);
        List<CurrencyRates> ratesForLastYear = currencyRateRepository.findSpecificRate(oneYear, endDate, from.concat(to));
        return mapper.toMonthlyAverageResponses(ratesForLastYear, from.concat(to));
    }

    /** Fetches historical monthly rates. */
    private List<CurrencyRatesResponse> fetchMonthlyRates(String from, String to) {
        LocalDate oneMonth = LocalDate.of(2025, 7, 19);
        LocalDate endDate = LocalDate.of(2025, 8, 19);
        List<CurrencyRates> ratesForLastMonth = currencyRateRepository.findSpecificRate(oneMonth, endDate, from.concat(to));
        return mapper.toCurrencyRatesResponses(ratesForLastMonth, from.concat(to), TimeSeries.MONTH);
    }

    /** Fetches historical weekly rates. */
    private List<CurrencyRatesResponse> fetchWeekRates(String from, String to) {
        LocalDate startDate = LocalDate.of(2025, 8, 13);
        LocalDate endDate = LocalDate.of(2025, 8, 19);
        List<CurrencyRates> ratesForLastWeek = currencyRateRepository.findSpecificRate(startDate, endDate, from.concat(to));
        return mapper.toCurrencyRatesResponses(ratesForLastWeek, from.concat(to), TimeSeries.WEEK);
    }
}
