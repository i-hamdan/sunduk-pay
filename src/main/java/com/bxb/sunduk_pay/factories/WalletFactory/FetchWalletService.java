package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to fetch main wallet and
 * its active sub-wallets for a user.
 */

@RequiredArgsConstructor
@Service
public class FetchWalletService implements WalletOperation {
    /**
     * Validations utility for input validation and data retrieval.
     **/
    private final Validations validations;
    /**
     * Mapper to convert wallet entities to response DTOs.
     **/
    private final WalletMapper walletMapper;
    /**
     * InvestmentValidation utility for investment-related validations.
     **/
    private final InvestmentValidation investmentValidation;
    /**
     * Repository to access SubWallet data.
     **/
    private final SubWalletRepository subWalletRepository;

    /**
     * Returns the request type handled by this service.
     *
     * @return request type FETCH_WALLET
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_WALLET;
    }

    /**
     * Fetches main wallet,
     * and non-deleted sub-wallets for a given user UUID.
     * Fetches main wallet and active (non-deleted)-
     * -sub-wallets for a given user UUID.
     *
     * @param mainWalletRequest request containing the user UUID
     * @return response containing wallet and sub-wallet details
     */
    @Override
    public MainWalletResponse perform(
            final MainWalletRequest mainWalletRequest) {
        MainWallet mainWallet = validations.
                getMainWalletInfo(mainWalletRequest.getUuid());

        List<SubWallet> subWallets = subWalletRepository
                .findAllByMainWalletMainWalletIdAndIsDeletedFalse(
                        mainWallet.getMainWalletId());

        List<SubWalletResponse> subWalletResponseList = walletMapper
                .toSubWalletResponseList(subWallets);

        List<SubWalletResponse> list = subWalletResponseList.stream()
                .map(subWallet -> {
                    if(subWallet.getIsInvested()) {
                        Investment investment = investmentValidation
                                .getInvestmentBySubWalletId(subWallet.getSubWalletId());
                        subWallet.setGainOrLossPercentage(investment.getProfitLossPercentage()
                                .toString());
                        subWallet.setRiskLevel(investment.getRiskLevel().toString());
                        return subWallet;
                    } else {
                        return subWallet;
                    }
                }).toList();

        return walletMapper.toWalletResponse(mainWallet, list);
    }
}
