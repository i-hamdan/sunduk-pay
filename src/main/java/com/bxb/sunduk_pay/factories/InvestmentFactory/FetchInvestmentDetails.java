package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.Mappers.InvestmentMapper;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentGraphDataDTO;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.FetchInvestmentActionType;
import com.bxb.sunduk_pay.util.InvestmentGraphData;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for fetching investment details.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FetchInvestmentDetails implements InvestmentOperation {

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
     * Returns the investment request type handled by this service.
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.FETCH_INVESTMENTS;
    }

    /**
     * Fetch investment details method
     */
    @Override
    public InvestmentResponse perform(InvestmentRequest investmentRequest) {
        log.info("Fetching investment details for User UUID: {}",
                investmentRequest.getUuid());
        User user = validations.getUserInfo(investmentRequest.getUuid());

        if (investmentRequest.getFetchInvestmentActionType()
                .equals(FetchInvestmentActionType.POT_INVESTMENTS)) {
            return fetchPotInvestments(user,
                    investmentRequest.getSubWalletId());
        } else if (investmentRequest.getFetchInvestmentActionType()
                .equals(FetchInvestmentActionType.ALL_INVESTMENTS)) {
            return fetchAllInvestments(user);
        } else throw new InvalidPayloadException(
                "Invalid fetch investment action type provided."
        );
    }


    /**
     * Fetch pot investments for the user.
     *
     * @return InvestmentResponse containing the fetched pot investments
     * @Param user the user whose investments are to be fetched
     * @Param investmentRequest the investment request containing fetch details
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

        Map<String, List<InvestmentGraphDataDTO>> monthlyGraphData =
                investmentGraphData
                .getMonthlyGraphData(transactionRepository
                .findAllByUserUuidAndWalletId(user.getUuid(),
                        subWallet.getSubWalletId()));

        InvestmentResponse investmentResponse = investmentMapper
                .toInvestmentResponse(investment,monthlyGraphData);

        investmentResponse.setMessage("Pot investment fetched successfully.");
        log.info(
                "Pot investments fetched successfully for User UUID: {}",
                user.getUuid());
        return investmentResponse;
    }

    /**
     * Fetch all investments for the user.
     *
     * @return InvestmentResponse containing all fetched investments
     * @Param user the user whose investments are to be fetched
     */
    private InvestmentResponse fetchAllInvestments(final User user) {
        List<Investment> investments = stockValidation
                .getInvestmentsByUserUuid(user.getUuid());


        return new InvestmentResponse();
    }


}
