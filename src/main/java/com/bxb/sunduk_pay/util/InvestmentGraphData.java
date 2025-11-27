package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Placeholder class for InvestmentGraphData.
 */
@Component
@Log4j2
public class InvestmentGraphData {


/**     * Processes a list of transactions to generate monthly graph data.
     *
     * @param transactions the list of transactions
     * @return a map with month names as keys and lists of
 * InvestmentGraphDataDTO as values
     */
    public Map<String, List<InvestmentGraphDataDTO>> getMonthlyGraphData(
            List<Transaction> transactions){
        // Temporary map grouped by month number (1–12)
        Map<Integer, List<InvestmentGraphDataDTO>> tempMap = new HashMap<>();

        for (Transaction txn : transactions) {

            int monthNumber = txn.getDateTime().getMonthValue(); // 1–12

            InvestmentGraphDataDTO dto = InvestmentGraphDataDTO.builder()
                    .balance(txn.getRemainingBalance())
                    .date(txn.getDateTime())
                    .build();

            tempMap.computeIfAbsent(monthNumber, m -> new ArrayList<>())
                    .add(dto);
        }

        // Final sorted map with keys: Jan, Feb, Mar…
        Map<String, List
                <InvestmentGraphDataDTO>> sortedMap = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            if (tempMap.containsKey(month)) {
                String monthName = LocalDate.of(2025, month, 1)
                        .format(DateTimeFormatter.ofPattern("MMM"));

                sortedMap.put(monthName, tempMap.get(month));
            }
        }

        return sortedMap;
    }
}
