package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Placeholder class for InvestmentGraphData.
 */
@Component
@Log4j2
public class InvestmentGraphData {

    /**
     * Year constant for date formatting.
     */
    private static final int YEAR = 2025;
    /**
     * Day constant for date formatting.
     */
    private static final int DAY = 1;
    /**
     * Day of month constant for date formatting.
     */
    private static final int dayOfMonth = 5;

    /**
     * Date formatter for "dd MMMM yyyy" pattern in English locale.
     */
    private static final DateTimeFormatter dateFormater =
            DateTimeFormatter.ofPattern("dd MMMM yyyy")
                    .withLocale(Locale.ENGLISH);

    /**
     * Processes a list of transactions to generate monthly graph data.
     *
     * @param transactions the list of transactions
     * @return a map with month names as keys and lists of
     * InvestmentGraphDataDTO as values
     */
    public Map<String, List<InvestmentGraphDataDTO>> withdrawalTrendsGraphData(
            final List<Transaction> transactions) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
                        "dd MMMM yyyy HH:mm:ss")
                .withLocale(Locale.ENGLISH);

        Map<Integer, TreeMap<LocalDateTime, List<InvestmentGraphDataDTO>>> tempMap
                = new TreeMap<>();

        for (Transaction txn : transactions) {
            int monthNumber = txn.getDateTime().getMonthValue();
            LocalDateTime dateTime = txn.getDateTime(); // full timestamp

            InvestmentGraphDataDTO dto = InvestmentGraphDataDTO.builder()
                    .balance(txn.getRemainingBalance())
                    .rawDateTime(dateTime)
                    .rawDate(dateTime.toLocalDate())
                    .date(dateTime.format(dateTimeFormatter))
                    .build();

            tempMap
                    .computeIfAbsent(monthNumber, m -> new TreeMap<>())
                    .computeIfAbsent(dateTime, d -> new ArrayList<>())
                    .add(dto);
        }

        Map<String, List<InvestmentGraphDataDTO>> finalMap = new LinkedHashMap<>();

        tempMap.forEach((month, dateMap) -> {
            String monthName = LocalDate.of(
                            LocalDate.now().getYear(), month, DAY)
                    .format(DateTimeFormatter.ofPattern("MMM"));

            List<InvestmentGraphDataDTO> dtoList = new ArrayList<>();
            dateMap.forEach((dt,
                             list) -> dtoList.addAll(list));

            // final deterministic sort: by exact timestamp,
            // then by balance (or any tie-breaker)
            dtoList.sort(Comparator
                    .comparing(InvestmentGraphDataDTO::getRawDateTime)
                    .thenComparing(Comparator.comparingDouble(
                            d -> d.getBalance() == null ? 0.0 : d.getBalance()))
            );

            finalMap.put(monthName, dtoList);
        });

        return finalMap;
    }


    /**
     * Processes a list of investment daily history records
     * to generate daily investment graph data.
     *
     * @param historyList the list of investment daily history records
     * @return a map with month names as keys and lists of
     * InvestmentGraphDataDTO as values
     */
    public Map<String, List<InvestmentGraphDataDTO>> dailyInvestmentGraphData(
            final List<InvestmentDailyHistory> historyList) {

        // AUTO-SORT months
        Map<Integer, TreeMap<LocalDate,
                InvestmentGraphDataDTO>> tempMap = new TreeMap<>();

        for (InvestmentDailyHistory history : historyList) {

            int monthNumber = history.getSnapshotDate().getMonthValue();
            LocalDate rawDate = history.getSnapshotDate();

            InvestmentGraphDataDTO dto = InvestmentGraphDataDTO.builder()
                    .rawDate(rawDate)
                    .date(rawDate.format(dateFormater))
                    .balance(history.getCurrentValue())
                    .build();

            tempMap.computeIfAbsent(monthNumber, m -> new TreeMap<>())
                    .put(rawDate, dto);  // AUTO-SORT days
        }

        // Final map with month names
        Map<String, List<InvestmentGraphDataDTO>> finalMap
                = new LinkedHashMap<>();

        for (Map.Entry<Integer, TreeMap<LocalDate,
                InvestmentGraphDataDTO>> entry : tempMap.entrySet()) {
            int month = entry.getKey();

            String monthName = LocalDate.of(YEAR, month, dayOfMonth)
                    .format(DateTimeFormatter.ofPattern("MMM"));

            finalMap.put(
                    monthName,
                    entry.getValue().values().stream()
                            .sorted(Comparator.comparing(
                                    InvestmentGraphDataDTO::getRawDate))
                            .toList()
            );

        }

        return finalMap;
    }


    /**
     * Processes a map of portfolio history to generate
     * daily combined investment graph data.
     *
     * @param portfolioHistory the map of portfolio history
     * @return a map with month names as keys and lists of
     * InvestmentGraphDataDTO as values
     */
    public Map<String,
            List<InvestmentGraphDataDTO>> dailyCombinedInvestmentGraphData(
            final Map<LocalDate, Double> portfolioHistory) {

        // Temporary: monthNumber → (date → DTO)
        Map<Integer, TreeMap<LocalDate, InvestmentGraphDataDTO>> tempMap =
                new TreeMap<>();

        for (Map.Entry<LocalDate,
                Double> entry : portfolioHistory.entrySet()) {

            LocalDate rawDate = entry.getKey();
            Double totalBalance = entry.getValue();

            int monthNumber = rawDate.getMonthValue();

            InvestmentGraphDataDTO dto = InvestmentGraphDataDTO.builder()
                    .rawDate(rawDate)
                    .date(rawDate.format(dateFormater))  // "dd MMMM yyyy"
                    .balance(totalBalance)
                    .build();

            tempMap.computeIfAbsent(monthNumber, m -> new TreeMap<>())
                    .put(rawDate, dto);
        }

        // Final: "Jan" → list of sorted DTOs
        Map<String,
                List<InvestmentGraphDataDTO>> finalMap = new LinkedHashMap<>();

        for (Map.Entry<Integer, TreeMap<LocalDate,
                InvestmentGraphDataDTO>> entry : tempMap.entrySet()) {

            int monthNumber = entry.getKey();

            String monthName = LocalDate.of(LocalDate.now().getYear(),
                            monthNumber, DAY)
                    .format(DateTimeFormatter.ofPattern("MMM"));

            finalMap.put(
                    monthName,
                    entry.getValue().values().stream()
                            .sorted(Comparator.comparing(
                                    InvestmentGraphDataDTO::getRawDate))
                            .toList()
            );

        }

        return finalMap;
    }


}
