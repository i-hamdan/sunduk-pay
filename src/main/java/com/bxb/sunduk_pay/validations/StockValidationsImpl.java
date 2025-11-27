package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.repository.AssetPriceRepository;
import com.bxb.sunduk_pay.postgress.repository.PortfolioModelRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of stock-related validations.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class StockValidationsImpl implements StockValidation{
    /** Repository for investment data access. */
    private final InvestmentRepository investmentRepository;
/** Repository for portfolio model data access. */
    private final PortfolioModelRepository portfolioModelRepository;
/** Repository for stock unit prices. */
    private final StockUnitPriceRepository stockUnitPriceRepository;
/** Repository for asset prices. */
    private final AssetPriceRepository assetPriceRepository;
/** Gets the stock unit price for a given date.
 * @param date the date for which to retrieve the stock unit price
 * @return the stock unit price for the specified date
 */

    @Override
    public StockUnitPrice getStockUnitPriceByDate(LocalDate date) {
      return stockUnitPriceRepository.findByDate(date).orElseThrow(() ->
                new ResourceNotFoundException("No stock unit price "
                        + "found for the given date: " + date)
        );
    }
    /** Validates if the balance is sufficient for investment.
     * @param balance the balance to validate
     */
    @Override
    public void ValidateBalanceForInvestment(Double balance) {
       if (balance <= 0|| balance == null) {
           throw new InsufficientBalanceException
                   ("Insufficient balance for investment.");
       }
    }

    @Override
    public PortfolioModel validatePortfolioModelByName(String name) {
     return portfolioModelRepository.findByName(name).orElseThrow(() ->
            new ResourceNotFoundException("Portfolio model not found with name: "
                    + name));
    }

    @Override
    public List<AssetPrice> getAssetClosestPrice(Long id, LocalDateTime date) {
        return assetPriceRepository.findClosestPrice(id,date);

    }

    @Override
    public AssetPrice getClosestOrLatestPrice(Long assetId, LocalDateTime date) {
        // Try closest price first
        List<AssetPrice> closestList =
                assetPriceRepository.findClosestPrice(assetId, date);

        if (!closestList.isEmpty()) {
            return closestList.get(0);
        }

        // Fallback → latest price
        return assetPriceRepository
                .findLatestByAsset(assetId)
                .orElse(null);
    }

    @Override
    public Investment getInvestmentBySubWalletId(String subWalletId) {
        return investmentRepository.findBySubWalletSubWalletIdAndIsActiveTrue(
                subWalletId).orElseThrow(
                        ()->new InvestmentException("Cannot find active "
                                +"investment for this pot!" ));
    }

    @Override
    public List<Investment> getInvestmentsByUserUuid(String uuid) {
        try {
            log.info("Fetching investments for user UUID: {}", uuid);
            List<Investment> investments = investmentRepository
                    .findByUserUuid(uuid);
            log.info("Fetched {} investments for user UUID: {}",
                    investments.size(), uuid);

            if (investments.isEmpty()){
                log.error("No investments found for user UUID: {}",
                        uuid);
                throw new InvestmentException(
                        "No investments found for user with UUID: " + uuid);
            }

            return investments;

        } catch (Exception e) {
            throw new InvestmentException(
                    "Error fetching investments for user:"
                    + e.getMessage());
        }
    }
}
