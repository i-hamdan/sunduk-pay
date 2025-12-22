package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.CurrencyMapper;
import com.bxb.sunduk_pay.exception.CustomExchangeRateException;
import com.bxb.sunduk_pay.exception.InvalidCurrencyType;
import com.bxb.sunduk_pay.exception.NullAmountException;
import com.bxb.sunduk_pay.repository.CurrencyRepository;
import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyRatesResponse;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import com.bxb.sunduk_pay.util.TimeSeries;
import lombok.RequiredArgsConstructor;
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
 * Service implementation for currency conversion.
 * and fetching historical exchange rates.
 * Handles real-time conversion using external API
 * and historical data from the database.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    /** Fixed fee deducted from converted amount. */
    private static final double FEE = 0;
    /** Start year for historical data retrieval. */
    private static final int YEAR_START = 2024;
    /** End year for historical data retrieval. */
    private static final int YEAR_END = 2025;
    /** Month constant for August. */
    private static final int MONTH_END = 8;
    /** Month constant for July. */
    private static final int MONTH_START = 7;
    /** Day constant for 19th. */
    private static final int DAY_19 = 19;
    /** Day constant for 13th. */
    private static final int DAY_13 = 13;

    /** CurrencyMapper for mapping entities to DTOs. */
    private final CurrencyMapper mapper;
    /** Repository for accessing currency rates. */
    private final CurrencyRepository currencyRateRepositoryImpl;
    /** Base URL for the external exchange rate API. */
    @Value("${exchange.api.url}")
    private String exchangeApiUrl;
    /** RestTemplate for making HTTP requests. */
    private final RestTemplate restTemplate;



    /**
     * Converts an amount from one currency to another and
     * optionally fetches historical rates.
     *
     * @param currencyRequest request containing source currency,
     *                        target currency, amount, and time series
     * @return CurrencyResponse containing converted amount,
     * exchange rate, fees, and historical rates
     * @throws NullAmountException       if amount is null
     * @throws InvalidCurrencyType       if the currency is invalid
     * @throws CustomExchangeRateException if API response is invalid
     */
    @Override
    public CurrencyResponse convertCurrency(
            final CurrencyRequest currencyRequest) {

        double exchangeRate = fetchExchangeRate(
                currencyRequest.getFromCurrency(),
                currencyRequest.getToCurrency());
        log.debug("Exchange rate fetched successfully: {}",
                exchangeRate);

        if (currencyRequest.getAmount() == null) {
            log.error("Invalid amount received in request: {}",
                    currencyRequest.getAmount());
            throw new NullAmountException(
                    "Amount Cannot Be null !" + currencyRequest.getAmount());
        }

        log.debug("Converting amount {} with exchange rate {}",
                currencyRequest.getAmount(), exchangeRate);

        double converted = currencyRequest.getAmount() * exchangeRate;
        log.debug("Converted amount = {}", converted);

        double fee = FEE;
        log.debug("Applying fee deduction of {}", fee);

        double finalAmount = converted - fee;
        log.debug("Final amount after fee deduction = {}", finalAmount);

        if (currencyRequest.getTimeSeries() == TimeSeries.WEEK) {
            List<CurrencyRatesResponse> weeklyRates = fetchWeekRates(
                    currencyRequest.getFromCurrency(),
                    currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate,
                    converted,
                    fee,
                    finalAmount,
                    null,
                    null,
                    weeklyRates);
        } else if (currencyRequest.getTimeSeries() == TimeSeries.MONTH) {
            List<CurrencyRatesResponse> monthlyRates = fetchMonthlyRates(
                    currencyRequest.getFromCurrency(),
                    currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate,
                    converted,
                    fee,
                    finalAmount,
                    null,
                    monthlyRates,
                    null);
        } else if (currencyRequest.getTimeSeries() == TimeSeries.YEAR) {
            List<CurrencyRatesResponse> yearlyRates = fetchYearlyRates(
                    currencyRequest.getFromCurrency(),
                    currencyRequest.getToCurrency());
            return mapper.currencyResponse(exchangeRate,
                    converted,
                    fee,
                    finalAmount,
                    yearlyRates,
                    null,
                    null);
        } else {
            throw new ResourceNotFoundException("please provide Time Series");
        }
    }
    /**
     * Fetches the real-time exchange rate from external API.
     *
     * @param from source currency
     * @param to   target currency
     * @return exchange rate
     */
    private double fetchExchangeRate(final String from, final String to) {
        String url = exchangeApiUrl + "/latest" + "/" + from;

        log.debug(
                "Preparing to fetch exchange rate from API for {} to {}",
                from, to);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.getForEntity(url, Map.class);
            log.debug("API call successful, response received");
        } catch (Exception e) {
            log.error("API call failed for fromCurrency={} with error: {}",
                    from,
                    e.getMessage());
            throw new InvalidCurrencyType(
                    "API call failed for fromCurrency={} with error: {}"
                            + e.getMessage());
        }

        Map<String, Object> body = response.getBody();
        log.debug("Parsing API response body: {}", from);

        if (body == null) {
            log.error(
                    "Response from exchange rate API is null!");
            throw new CustomExchangeRateException(
                    "Empty response from exchange rate API");
        }

        if (!body.containsKey("conversion_rates")) {
            log.error(
                    "Missing 'conversion_rates' in response from exchange api");
            throw new CustomExchangeRateException("Missing 'conversion_rates' "
                    + "in response from external api");
        }

        Map<String, Object> rates = (Map<String, Object>)
                body.get("conversion_rates");
        log.debug("Extracted conversion_rates: {}", from, to);

        if (!rates.containsKey(to)) {
            log.error("Currency {} not found in conversion rates", to);
            throw new InvalidCurrencyType("Currency '" + to
                    + "' not found in conversion rates");
        }

        double rate = Double.parseDouble(rates.get(to).toString());
        log.info("Exchange rate for {} to {}: {}", from, to, rate);
        return rate;
    }

    /**
     * Fetches historical yearly rates.
     *
     * @param from source currency
     * @param to   target currency
     * @return list of yearly currency rates
     */
    private List<CurrencyRatesResponse> fetchYearlyRates(
            final String from,
            final String to) {
    String currencyPair = from.concat(to);
    LocalDate oneYear = LocalDate.of(YEAR_START, MONTH_END, DAY_19);
    LocalDate endDate = LocalDate.of( YEAR_END, MONTH_END, DAY_19);
    log.info("Fetching yearly rates for currencyPair={} from date={}",
                currencyPair, oneYear);


        List<Map<String, Object>> results = currencyRateRepositoryImpl
                .findSpecificRate(oneYear, endDate, currencyPair);
    return mapper.toMonthlyAverageResponses(results,
            currencyPair);

    }



    /**     * Fetches historical monthly rates.
     * @param from source currency
     * @param to   target currency
     * @return list of monthly currency rates
     */
    private List<CurrencyRatesResponse> fetchMonthlyRates(final String from,
                                                          final String to) {
        String currencyPair = from.concat(to);
        LocalDate oneMonth = LocalDate.of(
                YEAR_END, MONTH_START, DAY_19);
        LocalDate endDate = LocalDate.of(
                YEAR_END, MONTH_END, DAY_19);

        log.info(
                "Fetching monthly rates for currencyPair={} from date={}",
                currencyPair,
                oneMonth);

        List<Map<String, Object>> results = currencyRateRepositoryImpl
                .findSpecificRate(oneMonth, endDate, currencyPair);


    return mapper.toCurrencyRatesResponses(results,
            currencyPair,
            TimeSeries.MONTH);
    }


    /**     * Fetches historical weekly rates.
     * @param from source currency
     * @param to   target currency
     * @return list of weekly currency rates
     */
    private List<CurrencyRatesResponse> fetchWeekRates(
            final String from,
            final String to) {
        String currencyPair = from.concat(to);
        LocalDate startDate = LocalDate.of(
                YEAR_END, MONTH_END, DAY_13);
        LocalDate endDate = LocalDate.of(
                YEAR_END, MONTH_END, DAY_19);

        log.info("Fetching weekly rates for currencyPair={} from date={}",
                currencyPair,
                startDate);

        List<Map<String, Object>> results = currencyRateRepositoryImpl
                .findSpecificRate(startDate, endDate, currencyPair);

     return mapper.toCurrencyRatesResponses(results,
            currencyPair,
            TimeSeries.WEEK);
    }
}
