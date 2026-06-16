package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.Mappers.InvestmentMapper;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentsFetchType;
import com.bxb.sunduk_pay.util.InvestmentGraphData;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service for fetching investment details.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FetchInvestmentDetails implements InvestmentOperation {

    /** Decimal format for currency representation. */
    private static final DecimalFormat DECIMAL_FORMAT =
            new DecimalFormat("#,##0.00");

    /** Constant for percentage calculations. */
    private static final int HUNDRED = 100;
    /** Constant for six months duration. */
    private static final int SIX = 6;
    /** Constant for one month duration. */
    private static final int ONE = 1;



    /**
     * Stock-related validations.
     **/
    private final InvestmentValidation stockValidation;
    /**
     * Validations utility for input validation and data retrieval.
     **/
    private final Validations validations;

    /**
     * Mapper for investment entities and DTOs.
     **/
    private final InvestmentMapper investmentMapper;

    /**
     * Repository for investment data access.
     **/
    private final InvestmentGraphData investmentGraphData;

    /**
     * Repository for investment data access.
     **/
    private final TransactionRepository transactionRepository;

    /**
     * Repository for investment daily history data access.
     **/
    private final InvestmentDailyHistoryRepository investmentHistoryRepository;

    /**
     * Returns the investment request type handled by this service.
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.FETCH_INVESTMENTS;
    }

    /**
     * Fetch investment details method.
     */
    @Override
    public InvestmentResponse perform(
            final InvestmentRequest investmentRequest) {
        log.info("Fetching investment details for User UUID: {}",
                investmentRequest.getUuid());
        User user = validations.getUserInfo(investmentRequest.getUuid());

        if (InvestmentsFetchType.POT_INVESTMENTS
                .equals(investmentRequest.getInvestmentsFetchType())) {
            return fetchPotInvestments(user,
                    investmentRequest.getSubWalletId());
        } else if ((InvestmentsFetchType.ALL_INVESTMENTS
                .equals(investmentRequest.getInvestmentsFetchType()))) {
            return fetchAllInvestments(user);
        } else {
            throw new InvalidPayloadException(
                "Invalid fetch investment action type provided.");
        }
    }


    /**
     * Fetch pot investments for the user.
     * @param user the user whose investments are to be fetched
     * @param subWalletId the sub-wallet ID associated with the pot
     * @return InvestmentResponse containing fetched pot investments.
     *
     */
    private InvestmentResponse fetchPotInvestments(
            final User user,
            final String subWalletId) {
        log.info("Fetching pot investments for User UUID: {}",
                user.getUuid());

        log.info("Fetching Main Wallet for User UUID: {}",
                user.getUuid());
        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
        log.info("Main Wallet fetched successfully");

        log.info("Fetching Sub Wallet with ID: {}",
                subWalletId);
        SubWallet subWallet = validations.findSubWalletIfExists(
                mainWallet.getMainWalletId(), subWalletId);
        log.info("Sub Wallet fetched successfully");

        log.info("Fetching Investment for Sub Wallet ID: {}",
                subWallet.getSubWalletId());
        Investment investment = stockValidation.getInvestmentBySubWalletId(
                subWallet.getSubWalletId());
        log.info("Investment fetched successfully");

        Map<String, List<InvestmentGraphDataDTO>> trendsGraphData =
                investmentGraphData
                .withdrawalTrendsGraphData(transactionRepository
                .findAllByUserUuidAndWalletId(user.getUuid(),
                        subWallet.getSubWalletId()));

        Map<String, List<InvestmentGraphDataDTO>> dailyInvestmentGraphData =
                investmentGraphData
                        .dailyInvestmentGraphData(
                     investmentHistoryRepository.findByInvestmentInvestmentId(
                             investment.getInvestmentId()));

        InvestmentResponse investmentResponse = investmentMapper
                .toInvestmentResponse(
                        investment,
                        trendsGraphData,
                        dailyInvestmentGraphData);

        investmentResponse.setMessage("Pot investment fetched successfully.");
        log.info(
                "Pot investments fetched successfully for User UUID: {}",
                user.getUuid());
        return investmentResponse;
    }


    /**
     * Fetch all investments for the user.
     * @param user the user whose investments are to be fetched
     * @return InvestmentResponse containing all fetched investments.
     */
    private InvestmentResponse fetchAllInvestments(final User user) {

        List<Investment> investments =
                stockValidation.getInvestmentsByUserUuid(user.getUuid());

        if (investments.isEmpty()) {
            return InvestmentResponse.builder()
                    .totalInvestedAmount(0.0)
                    .totalCurrentValue(DECIMAL_FORMAT.format(0.0))
                    .totalNetProfitLoss(0.0)
                    .gain1MonthPercent(0.0)
                    .gain6MonthsPercent(0.0)
                    .build();
        }


        double totalInvested = investments.stream()
                .mapToDouble(Investment::getInvestmentAmount)
                .sum();

        double totalCurrentValue = investments.stream()
                .mapToDouble(Investment::getCurrentValue)
                .sum();

        //  Net profit or loss
        double totalNetProfitLoss = totalCurrentValue - totalInvested;

        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minusMonths(SIX);

        List<InvestmentDailyHistory> history =
                investmentHistoryRepository
                        .findByUserUuidAndSnapshotDateBetween(
                                user.getUuid(),
                                sixMonthsAgo,
                                today
                        );

        // If no history is found, return default response
        if (history == null || history.isEmpty()) {
            return InvestmentResponse.builder()
                    .totalInvestedAmount(totalInvested)
                    .totalCurrentValue(DECIMAL_FORMAT.format(totalCurrentValue))
                    .totalNetProfitLoss(totalNetProfitLoss)
                    .gain1MonthPercent(0.0)
                    .gain6MonthsPercent(0.0)
                    .build();
        }

        Map<LocalDate, Double> portfolioHistory = history.stream()
                .collect(Collectors.groupingBy(
                        InvestmentDailyHistory::getSnapshotDate,
                        TreeMap::new,
       Collectors
               .summingDouble(
                   h -> h.getUnits() * h.getUnitPrice())
                ));


        // If portfolio history is empty, return default response
        if (portfolioHistory.isEmpty()) {
            return InvestmentResponse.builder()
                    .totalInvestedAmount(totalInvested)
                    .totalCurrentValue(DECIMAL_FORMAT.format(totalCurrentValue))
                    .totalNetProfitLoss(totalNetProfitLoss)
                    .gain1MonthPercent(0.0)
                    .gain6MonthsPercent(0.0)
                    .build();
        }

        Map<String, List<InvestmentGraphDataDTO>>
                dailyCombinedInvestmentGraphData =
                investmentGraphData.dailyCombinedInvestmentGraphData(
                        portfolioHistory);


        TreeMap<LocalDate, Double> sorted = new TreeMap<>(portfolioHistory);

        double todayValue = sorted.lastEntry().getValue();


        // ---- inside fetchAllInvestments after 'sorted' and todayValue ----
        LocalDate oneMonthTarget = today.minusMonths(ONE);
        LocalDate sixMonthsTarget = today.minusMonths(SIX);

// try floorEntry for targets, else fallback to earliest entry
        Double oneMonthValue = valueAtOrBefore(sorted, oneMonthTarget);
        if (oneMonthValue == null) {
            oneMonthValue = sorted.firstEntry().getValue();
        }
        Double sixMonthsValue = valueAtOrBefore(sorted, sixMonthsTarget);
        if (sixMonthsValue == null) {
            sixMonthsValue = sorted.firstEntry().getValue();
        }
        double gain1MonthPercent = (oneMonthValue == null
                || oneMonthValue <= 0) ? 0
                : ((todayValue - oneMonthValue) / oneMonthValue) * HUNDRED;

        double gain6MonthsPercent = (sixMonthsValue == null
                || sixMonthsValue <= 0) ? 0
                : ((todayValue - sixMonthsValue) / sixMonthsValue) * HUNDRED;

        return InvestmentResponse.builder()
                .totalInvestedAmount(totalInvested)
                .totalCurrentValue(DECIMAL_FORMAT.format(totalCurrentValue))
                .totalNetProfitLoss(totalNetProfitLoss)
                .gain1MonthPercent(gain1MonthPercent)
                .gain6MonthsPercent(gain6MonthsPercent)
                .dailyInvestmentGraph(dailyCombinedInvestmentGraphData)
                .build();

    }

    /**
     * Helper method to get the value at or before a specific
     * date from a TreeMap.
     * @param map    The TreeMap containing date-value pairs.
     * @param target The target date to search for.
     * @return The value at or before the target date, or null if none found.
     */
    private Double valueAtOrBefore(
            final TreeMap<LocalDate, Double> map,
            final LocalDate target) {
        Map.Entry<LocalDate, Double> e = map.floorEntry(target);
        return (e != null) ? e.getValue() : null;
    }



//    private double getNetCashflow(LocalDate start,
//    LocalDate end,
//    String userUuid) {
//        List<Transaction> txns =
//                transactionRepository.findByUserUuidAndDateTimeBetween(
//                userUuid, start.atStartOfDay(), end.plusDays(1)
//                .atStartOfDay());
//
//        return txns.stream()
//                .mapToDouble(t ->
//                        t.getTransactionType().equals(TransactionType.CREDIT)
//                                ? t.getAmount()     // money added
//                                : -t.getAmount()    // money removed
//                )
//                .sum();
//    }


}
