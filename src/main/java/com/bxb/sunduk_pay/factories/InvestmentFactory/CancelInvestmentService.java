package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.exception.InvestmentNotFoundException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CancelInvestmentService handles the cancellation of investments.
 */
@Service
@RequiredArgsConstructor
public class CancelInvestmentService implements InvestmentOperation {

    /**
     * The investment to be canceled.
     */
    private final InvestmentRepository investmentRepository;

    /**
     * Validations utility for investment operations.
     */
    private final Validations validations;

    /**
     * Repository for sub-wallet operations.
     */
    private final SubWalletRepository subWalletRepository;
    /**
     * Repository for transaction operations.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Returns the type of investment request this service handles.
     *
     * @return InvestmentRequestType.CANCEL_INVESTMENT
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CANCEL_INVESTMENT;
    }

    /**
     * Performs the cancellation of the investment
     * based on the provided request.
     *
     * @param investmentRequest the investment
     *                          request containing cancellation details
     * @return InvestmentResponse indicating the result of the cancellation
     */
    @Override
    public InvestmentResponse perform(final InvestmentRequest investmentRequest) {
        try {
            // Validate user information before proceeding with cancellation
            User user = validations.getUserInfo(investmentRequest.getUuid());

            SubWallet subWallet = validations.findSubWalletIfExists(
                    user.getMainWallet().getMainWalletId(),
                    investmentRequest.getSubWalletId());

            if (subWallet != null && subWallet.getIsInvested()) {
                Investment investment = investmentRepository
                        .findBySubWalletSubWalletIdAndIsActiveTrue(
                                subWallet.getSubWalletId()).orElseThrow(
                                () -> new InvestmentNotFoundException(
                                 "Investment not found for cancellation"));
                investment.setActive(false);
                investment.setCreatedAt(LocalDateTime.now());

                double balanceToDeduct = subWallet.getBalance() * 0.0;
                // cancellation fee

                subWallet.setIsInvested(false);
                subWallet.setIsCancelInvestment(true);
                subWallet.setBalance(subWallet.getBalance() - balanceToDeduct);

                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.randomUUID().toString())
                        .user(user)
                        .isMaster(false)
                        .isInvestment(true)
                        .amount(subWallet.getBalance())
                        .transactionType(TransactionType.CREDIT)
                        .transactionLevel(TransactionLevel.INVESTED)
                        .dateTime(LocalDateTime.now())
                        .fromWallet("Investment")
                        .toWallet(subWallet.getSubWalletName())
                        .toWalletId(subWallet.getSubWalletId())
                        .description("Investment cancellation refund to "
                                + subWallet.getSubWalletName())
                        .build();

                subWalletRepository.save(subWallet);
                transactionRepository.save(transaction);
                investmentRepository.save(investment);


            }
            return InvestmentResponse.builder()
                    .message("Investment cancelled successfully for pot "
                            + subWallet.getSubWalletName())
                    .isCancelInvestment(true)
                    .build();
        } catch (Exception e) {
            throw new InvestmentException(
                    "Error cancelling investment: " + e.getMessage());
        }
    }
}
