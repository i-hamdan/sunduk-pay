//package com.bxb.sunduk_pay.seeder;
//
//import com.bxb.sunduk_pay.postgress.model.Asset;
//import com.bxb.sunduk_pay.postgress.model.AssetPrice;
//import com.bxb.sunduk_pay.postgress.model.PortfolioAllocation;
//import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
//import com.bxb.sunduk_pay.postgress.repository.AssetPriceRepository;
//import com.bxb.sunduk_pay.postgress.repository.AssetRepository;
//import com.bxb.sunduk_pay.postgress.repository.PortfolioAllocationRepository;
//import com.bxb.sunduk_pay.postgress.repository.PortfolioModelRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * A comprehensive data seeder responsible for loading all initial
// * static data (Assets, Portfolio Models, and Allocations) and then
// * fetching and persisting historical market prices at application
// * startup. All steps are idempotent, ensuring data integrity upon
// * application restarts.
// */
//@Configuration
//@RequiredArgsConstructor
//public class AssetDataSeeder {
//
//    private static final Logger logger =
//            LoggerFactory.getLogger(AssetDataSeeder.class);
//
//    // Repositories for dynamic and static data
//    private final AssetRepository assetRepository;
//    private final AssetPriceRepository assetPriceRepository;
//    private final PortfolioModelRepository modelRepository;
//    private final PortfolioAllocationRepository allocationRepository;
//
//    // --- CONFIGURATION FOR API DOJO/RAPIDAPI (v2/get-chart) ---
//    private static final String API_URL_TEMPLATE =
//            "https://apidojo-yahoo-finance-v1.p.rapidapi.com/" +
//                    "stock/v2/get-chart?interval=1d&range=1y&symbol=%s&region=";
//
//    // !!! REPLACE WITH YOUR ACTUAL KEYS !!!
//    private static final String API_KEY =
//            "ad73556864msh3a621d64cf35d9ep1b7ccbjsn9591f1b3749b";
//    private static final String API_HOST =
//            "apidojo-yahoo-finance-v1.p.rapidapi.com";
//
//    private final HttpClient httpClient = HttpClient.newHttpClient();
//
//    /**
//     * Defines the CommandLineRunner to execute all seeding logic
//     * sequentially upon application startup.
//     * Order of execution: Assets -> Models/Allocations ->
//     * Historical Prices.
//     *
//     * @return A CommandLineRunner instance that seeds initial data.
//     */
//    @Bean
//    CommandLineRunner loadAllData() {
//        return args -> {
//            logger.info("--- Starting Comprehensive Data Seeder ---");
//
//            // 1. Seed Static Assets (required for Models and Prices)
//            seedAssets();
//
//            // 2. Seed Portfolio Models and Allocations (required for
//            // core functionality)
//            seedPortfolioModelsAndAllocations();
//
//            // 3. Seed Historical Prices (required for price tracking)
//            seedHistoricalPrices();
//
//            logger.info("--- Comprehensive Data Seeder Finished ---");
//        };
//    }
//
//    /**
//     * Seeds initial Asset data if the Asset table is empty
//     * (Idempotency check).
//     */
//    private void seedAssets() {
//        if (assetRepository.count() == 0) {
//            logger.info("Assets table is empty. Seeding initial Asset " +
//                    "data...");
//
//            assetRepository.save(Asset.builder()
//                    .name("SP Funds Dow Jones Global Sukuk ETF")
//                    .symbol("SPSK")
//                    .type("SUKUK")
//                    .build());
//
//            assetRepository.save(Asset.builder()
//                    .name("SPDR Gold Shares ETF")
//                    .symbol("GLD")
//                    .type("COMMODITY")
//                    .build());
//
//            assetRepository.save(Asset.builder()
//                    .name("Wahed FTSE USA Shariah ETF")
//                    .symbol("HLAL")
//                    .type("EQUITY")
//                    .build());
//
//            assetRepository.save(Asset.builder()
//                    .name("UAE Dirham (Cash)")
//                    .symbol("AED")
//                    .type("CASH")
//                    .build());
//
//            logger.info("Successfully seeded 4 Asset records.");
//        } else {
//            logger.info("Assets table already contains data. Skipping " +
//                    "asset seeding.");
//        }
//    }
//
//    /**
//     * Seeds Portfolio Models and their Allocations if the
//     * PortfolioModel table is empty (Idempotency check).
//     */
//    private void seedPortfolioModelsAndAllocations() {
//        if (modelRepository.count() == 0) {
//            logger.info("PortfolioModel table is empty. Seeding Models" +
//                    " and Allocations...");
//
//            // Fetch assets (They must exist now, thanks to the prior step)
//            Asset sukuk = assetRepository.findBySymbol("SPSK")
//                    .orElseThrow(() -> new IllegalStateException(
//                            "SPSK asset not found during model setup."));
//            Asset gold = assetRepository.findBySymbol("GLD")
//                    .orElseThrow(() -> new IllegalStateException(
//                            "GLD asset not found during model setup."));
//            Asset equity = assetRepository.findBySymbol("HLAL")
//                    .orElseThrow(() -> new IllegalStateException(
//                            "HLAL asset not found during model setup."));
//            Asset cash = assetRepository.findBySymbol("AED")
//                    .orElseThrow(() -> new IllegalStateException(
//                            "AED asset not found during model setup."));
//
//            /* ---------------------------
//             * LOW RISK MODEL
//             * --------------------------- */
//            PortfolioModel lowRisk = modelRepository.save(
//                    PortfolioModel.builder().name("Low Risk Model").build()
//            );
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(lowRisk).asset(sukuk)
//                    .weight(0.50).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(lowRisk).asset(gold)
//                    .weight(0.20).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(lowRisk).asset(equity)
//                    .weight(0.20).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(lowRisk).asset(cash)
//                    .weight(0.10).build());
//
//
//            /* ---------------------------
//             * MEDIUM RISK MODEL
//             * --------------------------- */
//            PortfolioModel mediumRisk = modelRepository.save(
//                    PortfolioModel.builder().name("Medium Risk Model").build()
//            );
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(mediumRisk).asset(sukuk)
//                    .weight(0.35).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(mediumRisk).asset(equity)
//                    .weight(0.40).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(mediumRisk).asset(gold)
//                    .weight(0.15).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(mediumRisk).asset(cash)
//                    .weight(0.10).build());
//
//
//            /* ---------------------------
//             * HIGH RISK MODEL
//             * --------------------------- */
//            PortfolioModel highRisk = modelRepository.save(
//                    PortfolioModel.builder().name("High Risk Model").build()
//            );
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(highRisk).asset(equity)
//                    .weight(0.65).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(highRisk).asset(gold)
//                    .weight(0.20).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(highRisk).asset(sukuk)
//                    .weight(0.10).build());
//            allocationRepository.save(PortfolioAllocation.builder()
//                    .portfolioModel(highRisk).asset(cash)
//                    .weight(0.05).build());
//
//            logger.info("Successfully seeded 3 PortfolioModels and 12 " +
//                    "PortfolioAllocations.");
//        } else {
//            logger.info("PortfolioModel table already contains data. " +
//                    "Skipping model and allocation seeding.");
//        }
//    }
//
//    /**
//     * Seeds historical prices for market assets by calling an external API.
//     * This step is idempotent, only saving new data points.
//     */
//    private void seedHistoricalPrices() {
//        logger.info("Starting historical asset price seeding process.");
//
//        Map<String, String> tickerMap = new HashMap<>();
//        tickerMap.put("EQUITY", "HLAL");
//        tickerMap.put("SUKUK", "SPSK");
//        tickerMap.put("COMMODITY", "GLD");
//
//        // Process Market Assets via API
//        for (Map.Entry<String, String> entry : tickerMap.entrySet()) {
//            String symbol = entry.getValue();
//
//            assetRepository.findBySymbol(symbol).ifPresent(asset -> {
//                try {
//                    logger.info("Fetching historical chart data for: {}",
//                            symbol);
//                    fetchAndSaveHistory(asset, symbol);
//                    // Sleep to respect Rate Limits
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (Exception e) {
//                    logger.error("Failed to seed history for {}: {}",
//                            symbol, e.getMessage(), e);
//                }
//            });
//        }
//        logger.info("Historical asset price seeding process completed.");
//    }
//
//    /**
//     * Executes the API call to fetch historical data and saves it only
//     * if the record does not already exist for the specific asset and
//     * effective date.
//     *
//     * @param asset The Asset entity to associate prices with.
//     * @param symbol The ticker symbol for the API call.
//     * @throws IOException If an I/O error occurs when sending or
//     * receiving.
//     * @throws InterruptedException If the operation is interrupted.
//     */
//    private void fetchAndSaveHistory(Asset asset, String symbol)
//            throws IOException, InterruptedException {
//
//        String url = String.format(API_URL_TEMPLATE, symbol);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("x-rapidapi-key", API_KEY)
//                .header("x-rapidapi-host", API_HOST)
//                .GET()
//                .build();
//
//        HttpResponse<String> response = httpClient.send(request,
//                HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 200) {
//            String jsonBody = response.body();
//            int savedCount = 0;
//            int skippedCount = 0;
//
//            // --- PARSING LOGIC FOR 'get-chart' ---
//
//            // 1. Extract Timestamp Array String
//            Pattern timestampPattern = Pattern.compile(
//                    "\"timestamp\"\\s*:\\s*\\[([\\d,\\s]*)\\]");
//            Matcher timestampMatcher =
//                    timestampPattern.matcher(jsonBody);
//
//            // 2. Extract Close Price Array String
//            Pattern closePattern = Pattern.compile(
//                    "\"close\"\\s*:\\s*\\[([\\d\\.,\\snull]*)\\]");
//            Matcher closeMatcher = closePattern.matcher(jsonBody);
//
//            if (timestampMatcher.find() && closeMatcher.find()) {
//                String[] timestamps = timestampMatcher.group(1).split(",");
//                String[] prices = closeMatcher.group(1).split(",");
//
//                // Loop through arrays (they correspond by index)
//                for (int i = 0; i < timestamps.length; i++) {
//                    String tsStr = timestamps[i].trim();
//                    String priceStr = (i < prices.length) ?
//                            prices[i].trim() : "null";
//
//                    // Skip nulls (markets closed or missing data)
//                    if (!tsStr.isEmpty() && !priceStr.isEmpty() &&
//                            !priceStr.equals("null")) {
//                        try {
//                            long timestamp = Long.parseLong(tsStr);
//                            BigDecimal price = new BigDecimal(priceStr);
//
//                            LocalDateTime effectiveAt =
//                                    LocalDateTime.ofInstant(
//                                            Instant.ofEpochSecond(timestamp),
//                                            ZoneId.systemDefault()
//                                    );
//
//                            // --- IDEMPOTENCY CHECK (for prices) ---
//                            Optional<AssetPrice> existingPrice =
//                                    assetPriceRepository
//                                            .findByAssetAndEffectiveAt(asset,
//                                                    effectiveAt);
//
//                            if (existingPrice.isPresent()) {
//                                skippedCount++;
//                                continue; // Skip saving
//                            }
//                            // --- END IDEMPOTENCY CHECK ---
//
//                            AssetPrice historicalPrice =
//                                    AssetPrice.builder()
//                                            .asset(asset)
//                                            .closePrice(price)
//                                            .effectiveAt(effectiveAt)
//                                            .build();
//
//                            assetPriceRepository.save(historicalPrice);
//                            savedCount++;
//                        } catch (NumberFormatException ex) {
//                            logger.warn("Parsing Error: Skipped data point " +
//                                    "for {} at index {} due to invalid " +
//                                    "number format.", symbol, i);
//                        }
//                    }
//                }
//                logger.info("Seeded {} new historical data points ({} " +
//                        "skipped) for {}", savedCount, skippedCount, symbol);
//            } else {
//                logger.error("Could not parse chart data structure for {}. " +
//                        "Check API response format.", symbol);
//            }
//
//        } else if (response.statusCode() == 429) {
//            logger.error("API Rate Limit Exceeded (429) for {}.", symbol);
//        } else {
//            logger.error("API Error {} for {}. Body: {}",
//                    response.statusCode(), symbol, response.body());
//        }
//    }
//}