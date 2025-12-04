package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service to handle updating the risk level of an investment.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateRiskLevelService implements InvestmentOperation {

    /**
     * Utility for validations.
     */
    private final Validations validations;

    /**
     * Utility for investment-specific validations.
     */
    private final InvestmentValidation investmentValidation;

    /**
     * Repository for investment persistence.
     */
    private final InvestmentRepository investmentRepository;


    /**
     * Returns the InvestmentRequestType handled by this service.
     *
     * @return InvestmentRequestType.CHANGE_RISK_LEVEL
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CHANGE_RISK_LEVEL;
    }

    /**
     * Updates the risk level of an investment associated with a sub-wallet.
     *
     * @param investmentRequest request containing user UUID,
     *                          sub-wallet ID, and new risk level
     * @return response indicating success
     */
    @Override
    public InvestmentResponse perform(InvestmentRequest investmentRequest) {

        User user = validations.getUserInfo(investmentRequest.getUuid());

        SubWallet subWallet = validations.findSubWalletIfExists(
                user.getMainWallet().getMainWalletId(),
                investmentRequest.getSubWalletId());

        Investment investment = investmentValidation.
                getInvestmentBySubWalletId(subWallet.getSubWalletId());

        if (Boolean.TRUE.equals(!subWallet.getIsInvested())
                || !investment.isActive()){
            throw new InvestmentException(
                    "Cannot change risk level for inactive investment.");
        }


        RiskLevel riskLevel = investmentValidation
                .validateRiskLevel(investment.getRiskLevel().toString(),
                        investmentRequest.getRiskLevel()
                        .toString());

        PortfolioModel portfolioModel = investmentValidation
                .getPortfolioModelByRiskLevel(riskLevel.toString());

        investment.setPortfolioModelId(portfolioModel.getId());
        investment.setRiskLevel(riskLevel);

        investmentRepository.save(investment);

        return InvestmentResponse.builder()
                .message("Risk level updated successfully to "
                        + investmentRequest.getRiskLevel()
                        .toString())
                .build();
    }
}
