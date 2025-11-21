package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.postgress.model.Asset;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import com.bxb.sunduk_pay.postgress.repository.AssetPriceRepository;
import com.bxb.sunduk_pay.postgress.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scheduled component responsible for running the daily asset price
 * fetching job. It fetches the latest closing price and ensures
 * that the price for a specific date is only persisted once
 * (idempotency) using SLF4J logging.
 */
@Component
public class DailyPriceScheduler {

    // Initialize the logger for this class
    private static final Logger logger = LoggerFactory
            .getLogger(DailyPriceScheduler.class);

    // --- CONFIGURATION FOR API DOJO/RAPIDAPI (v2/get-chart) ---
    private static final String API_URL_TEMPLATE =
            "https://apidojo-yahoo-finance-v1.p.rapidapi.com/" +
                    "stock/v2/get-chart?interval=1d&range=1d&symbol=%s&region=";

    // !!! REPLACE WITH YOUR ACTUAL KEYS OR USE application.properties !!!
    private static final String API_KEY =
            "ad73556864msh3a621d64cf35d9ep1b7ccbjsn9591f1b3749b";
    private static final String API_HOST =
            "apidojo-yahoo-finance-v1.p.rapidapi.com";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AssetRepository assetRepository;
    private final AssetPriceRepository assetPriceRepository;

    // List of tickers to be processed by the daily job
    private static final List<String> TARGET_TICKERS = Arrays.asList(
            "HLAL", // Wahed FTSE USA Shariah ETF (EQUITY)
            "SPSK", // SP Funds Dow Jones Global Sukuk ETF (SUKUK)
            "GLD"   // SPDR Gold Shares ETF (COMMODITY)
    );

    public DailyPriceScheduler(AssetRepository assetRepository,
                               AssetPriceRepository assetPriceRepository) {
        this.assetRepository = assetRepository;
        this.assetPriceRepository = assetPriceRepository;
    }

    /**
     * Scheduled job to fetch the latest closing price for all
     * configured assets. The cron expression "0 0 17 * * MON-FRI"
     * runs at 5:00 PM (17:00) every weekday.
     */
    @Scheduled(cron = "0 0 17 * * MON-FRI")
    public void runDailyPriceFetch() {
        logger.info("Starting daily asset price fetch job.");
        long startTime = System.currentTimeMillis();

        for (String symbol : TARGET_TICKERS) {
            processAssetLatestPrice(symbol);

            // Wait to respect API rate limits (2 seconds)
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Scheduler interrupted during sleep.", e);
            }
        }

        long endTime = System.currentTimeMillis();
        logger.info("Daily asset price fetch job finished in {} ms.",
                (endTime - startTime));
    }

    /**
     * Finds an asset by its symbol and attempts to fetch and save its
     * latest closing price.
     * @param symbol The ticker symbol of the asset.
     */
    private void processAssetLatestPrice(String symbol) {
        Optional<Asset> assetOptional = assetRepository.findBySymbol(symbol);

        if (assetOptional.isPresent()) {
            Asset asset = assetOptional.get();
            try {
                logger.info("-> Executing API call for asset: {}", symbol);
                fetchAndSaveLatestPrice(asset, symbol);
            } catch (Exception e) {
                logger.error("Failed to fetch price for {}: {}", symbol,
                        e.getMessage(), e);
            }
        } else {
            logger.error("Asset not found in database for symbol: {}", symbol);
        }
    }

    /**
     * Executes the API call, parses the JSON response, checks for
     * duplicates, and saves the price if new.
     *
     * IMPORTANT: This method assumes the AssetPriceRepository has a
     * method like:
     * `Optional<AssetPrice> findByAssetAndEffectiveAt(Asset asset,
     * LocalDateTime effectiveAt);`
     * You must ensure this method exists in your AssetPriceRepository
     * interface.
     */
    private void fetchAndSaveLatestPrice(Asset asset, String symbol)
            throws IOException, InterruptedException {

        String url = String.format(API_URL_TEMPLATE, symbol);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", API_HOST)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonBody = response.body();

            // 1. Extract Timestamp Array String
            Pattern timestampPattern = Pattern.compile(
                    "\"timestamp\"\\s*:\\s*\\[([\\d,\\s]*)\\]");
            Matcher timestampMatcher =
                    timestampPattern.matcher(jsonBody);

            // 2. Extract Close Price Array String
            Pattern closePattern = Pattern.compile(
                    "\"close\"\\s*:\\s*\\[([\\d\\.,\\snull]*)\\]");
            Matcher closeMatcher = closePattern.matcher(jsonBody);

            if (timestampMatcher.find() && closeMatcher.find()) {
                String[] timestamps = timestampMatcher.group(1).split(",");
                String[] prices = closeMatcher.group(1).split(",");

                if (timestamps.length > 0 && prices.length > 0) {
                    // Use the first element as it represents the latest
                    // available data point
                    String tsStr = timestamps[0].trim();
                    String priceStr = prices[0].trim();

                    if (!tsStr.isEmpty() && !priceStr.isEmpty() &&
                            !priceStr.equals("null")) {
                        try {
                            long timestamp = Long.parseLong(tsStr);
                            BigDecimal price = new BigDecimal(priceStr);

                            LocalDateTime effectiveAt = LocalDateTime.ofInstant(
                                    Instant.ofEpochSecond(timestamp),
                                    ZoneId.systemDefault()
                            );

                            // --- DUPLICATE CHECK LOGIC ---
                            Optional<AssetPrice> existingPrice =
                                    assetPriceRepository
                                            .findByAssetAndEffectiveAt(asset,
                                                    effectiveAt);

                            if (existingPrice.isPresent()) {
                                logger.warn("Skipped {}: Price for date {} " +
                                                "already exists in the database. " +
                                                "(ID: {})",
                                        symbol, effectiveAt.toLocalDate(),
                                        existingPrice.get().getId());
                                return;
                            }
                            // --- END DUPLICATE CHECK ---

                            AssetPrice latestPrice = AssetPrice.builder()
                                    .asset(asset)
                                    .closePrice(price)
                                    .effectiveAt(effectiveAt)
                                    .build();

                            assetPriceRepository.save(latestPrice);
                            logger.info("Successfully saved NEW latest price" +
                                            " for {}: {} at {}",
                                    symbol, price.toPlainString(), effectiveAt);

                        } catch (NumberFormatException ex) {
                            logger.error("Parsing Error: Invalid number " +
                                            "format in API response for {}.",
                                    symbol, ex);
                        }
                    } else {
                        logger.info("Skipped {}: Latest data point was null " +
                                "or empty (market closed/data gap).", symbol);
                    }
                } else {
                    logger.error("API response array was empty for {}.",
                            symbol);
                }

            } else {
                logger.error("Could not parse chart data structure for {}. " +
                                "Response body might be incomplete or malformed.",
                        symbol);
            }

        } else if (response.statusCode() == 429) {
            logger.error("API Rate Limit Exceeded (429) for {}.", symbol);
        } else {
            logger.error("API Error {} for {}. Body: {}",
                    response.statusCode(), symbol, response.body());
        }
    }
}