package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.postgress.model.AssetPrice;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StockValidation {
    StockUnitPrice getStockUnitPriceByDate(LocalDate date);
    void ValidateBalanceForInvestment(Double balance);
    PortfolioModel validatePortfolioModelByName(String name);
    List<AssetPrice>getAssetClosestPrice(Long id, LocalDateTime date);

    AssetPrice getClosestOrLatestPrice(Long assetId, LocalDateTime date);

    /** Retrieve investment by sub-wallet ID. */
    Investment getInvestmentBySubWalletId(String subWalletId);

    List<Investment>getInvestmentsByUserUuid(String uuid);
}
