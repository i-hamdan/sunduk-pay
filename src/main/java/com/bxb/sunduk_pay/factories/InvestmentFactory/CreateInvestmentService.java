package com.bxb.sunduk_pay.factories.InvestmentFactory;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.postgress.model.Asset;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import com.bxb.sunduk_pay.postgress.model.PortfolioAllocation;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.StockValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for creating investments.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateInvestmentService implements InvestmentOperation {
/** Validations utility for input validation and data retrieval. **/
    private final Validations validations;
    /** Stock-related validations. **/
    private final StockValidation stockValidation;
/** Repository for sub-wallet data access. **/
    private final SubWalletRepository subWalletRepository;
    /** Repository for investment data access. **/
    private final InvestmentRepository investmentRepository;
    /** Repository for transaction data access. **/
    private final TransactionRepository transactionRepository;

    /**
     * Returns the investment request type handled by this service.
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CREATE_INVESTMENT;
    }

    /**
     * Main investment creation method
     */
    @Transactional
    @Override
    public InvestmentResponse perform(InvestmentRequest request) {

        // 1. User fetch karna
        User user = validations.getUserInfo(request.getUuid());

        // 2. SubWallet fetch karna (ensure user ka hi wallet ho)
        SubWallet subWallet =
                validations.findSubWalletIfExists(
                        user.getMainWallet().getMainWalletId(),
                        request.getSubWalletId()
                );

        log.info("verifying sub-wallet: " + subWallet);

        // 3. Pot me paisa hai ya nahi? (zero balance allowed nahi)

        stockValidation.ValidateBalanceForInvestment(subWallet.getBalance());
        log.info("Balance validation passed for sub-wallet: "
                + subWallet.getSubWalletName());

        // 4. Check: ye pot already invested to nahi?
        if (!subWallet.getIsInvested()) {

            log.info("Creating investment for sub-wallet: "
                    + subWallet.getSubWalletName());

            // 5. Portfolio Model fetch karna based on Risk Level (LOW/MEDIUM/HIGH)
            PortfolioModel portfolioModel = stockValidation
                    .validatePortfolioModelByName(request.getRiskLevel().name());

            log.info("Fetched Portfolio Model: " + portfolioModel.getName());

            // 6. Total paisa jo user invest kar raha hai
            double potAmount = subWallet.getBalance();

            // Saare assets ke units yaha combine honge
            double totalCombinedUnits = 0;

            // 7. LocalDateTime me convert (price table LocalDateTime use karta hai)
            LocalDateTime assetDate =
                    LocalDateTime.now().minusMonths(10);

            // 8. Model ke andar kitne assets hai + unka weight
            List<PortfolioAllocation> allocations = portfolioModel.getAllocations();

            // 9. Har asset ke liye alag calculation (weight-wise)
            for (PortfolioAllocation alloc : allocations) {

            Asset asset = alloc.getAsset();     // ye kaun sa asset hai (HLAL, SPSK, GLD)
            Double weight = alloc.getWeight();  // kitna % allocation hai

                log.info("Processing asset: " + asset.getSymbol()
                        + " | weight: " + weight);

                // 10. Us date ka latest ya nearest asset price fetch karna
                AssetPrice assetPrice =
                        stockValidation.getClosestOrLatestPrice(
                                asset.getId(),
                                assetDate
                        );

        // this is to handle cases where no price is found eg(in cash assits)
            if (assetPrice == null) {
             log.warn("Skipping asset {} — No price available in DB",
             asset.getSymbol());
              continue;
                }


                // 11. Unit price (closing price from DB)
                double unitPrice = assetPrice.getClosePrice().doubleValue();

                // 12. Iss asset ko kitna paisa milega (based on weight)
                double allocatedMoney = potAmount * weight;

                // 13. Kitni units banti hain
                double units = allocatedMoney / unitPrice;

                log.info("Asset: " + asset.getSymbol()
                        + " | Allocated Money: " + allocatedMoney
                        + " | Unit Price: " + unitPrice
                        + " | Units: " + units);

                // 14. Total units add karte jao
                totalCombinedUnits += units;
            }

            // 15. Investment Object Create karna
            Investment investment = Investment.builder()
                    .user(user)
                    .portfolioModelId(portfolioModel.getId())// Kon sa model use hua
                    .investmentId(UUID.randomUUID().toString())
                    .investedAt(LocalDate.now())
                    .assetDate(assetDate)// this is for record purpose only
                    .subWallet(subWallet)              // Konse pot me investment hua
                    .investmentAmount(potAmount)       // Kitna paisa invest
                    .units(totalCombinedUnits)         // Total units sab assets se
                    .unitPriceAtPurchase(potAmount/totalCombinedUnits)
                    .updatedAt(LocalDateTime.now())
                    .currentValue(potAmount)           // Start me currentValue = investedAmount
                    .riskLevel(request.getRiskLevel()) // Risk Level
                    .isActive(true)
                    .build();

            log.info(investment);

            investmentRepository.save(investment);

            // 16. Pot ko mark karna: ab invest ho chuka hai
            subWallet.setIsInvested(true);
            subWalletRepository.save(subWallet);

            // 17. Transaction create karna (UI side me history dikhane ke liye)
            Transaction transaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(potAmount)
                    .transactionType(TransactionType.DEBIT) // Money pot se deducted
                    .transactionLevel(TransactionLevel.INVESTED)
                    .dateTime(LocalDateTime.now())
                    .description("Invested " + potAmount +
                            " using model " + portfolioModel.getName())
                    .fromWallet(subWallet.getSubWalletName())
                    .fromWalletId(subWallet.getSubWalletId())
                    .toWallet("Investment")
                    .isInvestment(true)
                    .isMaster(false)
                    .build();

            transactionRepository.save(transaction);

            // FINAL: Return success message
            return InvestmentResponse.builder()
                    .message("Great Job! Your investment is now active and growing.")
                    .build();

        } else {
            log.error("Investment creation failed. Sub-wallet: "
                    + subWallet.getSubWalletName()
                    + " is already invested.");
            throw new ResourceNotFoundException("Investment cannot be created. "
                    + "Either the sub-wallet does not exist or it is already invested.");
        }
    }

}
