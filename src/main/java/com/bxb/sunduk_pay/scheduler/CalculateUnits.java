package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.postgress.model.*;
import com.bxb.sunduk_pay.postgress.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j2

public class CalculateUnits {

    private final PortfolioModelRepository modelRepository;
    private final PortfolioAllocationRepository allocationRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final UnitsRepository unitRepository;
    /**
     * NEW FORMULA IMPLEMENTATION:
     *
     * For each model:
     *   1) Multiply all weights → combinedWeight
     *   2) For each asset:
     *          basePrice = earliest asset price
     *          todayPrice = price on current date
     *          normalized = todayPrice / basePrice
     *          contribution = normalized × combinedWeight
     *   3) Sum all contributions → final unit
     */

 private void  calculateUnitForDate(LocalDate date){

        log.info(" START calculating normalized units for {}", date);

        List<PortfolioModel> models = modelRepository.findAll();

        for (PortfolioModel model: models){

        log.info("---- MODEL {} ----", model.getName());

    List<PortfolioAllocation> allocations = allocationRepository
            .findByPortfolioModel(model);

            BigDecimal combinedWeight = BigDecimal.ONE;


            for (PortfolioAllocation alloc : allocations) {
                combinedWeight = combinedWeight
                        .multiply(BigDecimal.valueOf(alloc.getWeight()));
            }

            log.info("CombinedWeight (product of all weights) = {}",
                    combinedWeight);

            // total normalized unit
            BigDecimal totalUnit = BigDecimal.ZERO;
            int assetsUsed = 0;



            for (PortfolioAllocation alloc:allocations){
                Asset asset = alloc.getAsset();

                //  Get today's price
                AssetPrice todayPrice = assetPriceRepository
                        .findByAssetAndDate(
                        asset, date);


                if (todayPrice == null) {
                    log.warn("Skip {} — today's price missing for {}"
                            , asset.getSymbol(), date);
                    continue;
                }
                AssetPrice basePrice = assetPriceRepository
                        .findTopByAssetOrderByEffectiveAtAsc(asset);

            // normalizedPrice = todayPrice / basePrice
                BigDecimal normalized = todayPrice.getClosePrice()
                        .divide(basePrice.getClosePrice(), 10,
                                RoundingMode.HALF_UP);


                log.info("{}: normalized = {} / {} = {}",
                        asset.getSymbol(),
                        todayPrice.getClosePrice(),
                        basePrice.getClosePrice(),
                        normalized
                );

                // contribution = normalized × combinedWeight
                BigDecimal contribution = normalized.multiply(combinedWeight);

                log.info("{}: Contribution = normalized {} × combinedWeight {} = {}",
                        asset.getSymbol(), normalized, combinedWeight, contribution);

                totalUnit = totalUnit.add(contribution);
                assetsUsed++;

            }

            if (assetsUsed == 0) {
                log.warn(" No valid assets processed for model {} on {}",
                        model.getName(), date);
                continue;
            }
            log.info("Combine (generic) UNIT for model {} on {} = {}",
                    model.getName(), date, totalUnit);


            // 6️MULTIPLY BY 1000 BEFORE SAVING
            BigDecimal finalUnit = totalUnit.multiply(BigDecimal.valueOf(1000));

            log.info(" FINAL UNIT (unit × 1000) = {}", finalUnit);



            // Save result
            Units result = Units.builder()
                    .date(date)
                    .portfolioModel(model)
                    .combinedValue(finalUnit)
                    .build();

            unitRepository.save(result);

            log.info(" Saved → Model={} | Date={} | Unit={}",
                    model.getName(), date, totalUnit);

            }
        log.info(" DONE calculating normalized units for {}", date);
    }

    /**
     * Run for all dates in asset_price table
     */
    //@Scheduled(cron = "0 */2 * * * *")
    public void calculateForAllDates() {

        List<LocalDate> dates = assetPriceRepository.findAllUniqueDates()
                        .stream()
                .map(java.sql.Date::toLocalDate)
                        .toList();

        log.info("Found {} unique dates", dates.size());

        for (LocalDate d : dates) {
            calculateUnitForDate(d);
        }

        log.info(" All normalized unit calculations completed.");
    }


}
