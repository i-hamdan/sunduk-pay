package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.exception.CannotDeleteWalletException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


/**
 * Service to handle deletion of sub-wallets.
 * Only allows deletion if the sub-wallet balance is zero.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DeleteService implements WalletOperation {
    /** Validations utility for checking user and wallet info. */
    private final Validations validations;
    /** Repository for accessing and modifying main wallet data. */
    private final MainWalletRepository mainWalletRepository;


    /**
     * Returns the RequestType handled by this service.
     *
     * @return RequestType.DELETE
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE;
    }

    /**
     * Performs the deletion of a sub-wallet if balance is zero.
     *
     * @param mainWalletRequest request containing wallet and sub-wallet IDs
     * @return response with a success message
     * @throws CannotDeleteWalletException if the sub-wallet balance is not zero
     */
    @Override
    public MainWalletResponse perform(
            final MainWalletRequest mainWalletRequest) {
        try {
            log.info("Received request to delete SubWallet.");

            User user = validations.getUserInfo(mainWalletRequest.getUuid());
            log.debug("Fetched user info. userUuid={}, userName={}",
                    user.getUuid(), user.getFullName());

            validations.getMainWalletInfo(user.getUuid());
            log.debug("Validated MainWallet info for userUuid={}",
                    user.getUuid());

            MainWallet mainWallet = validations.getMainWalletByWalletId(
                    mainWalletRequest.getMainWalletId());
            log.debug("Fetched MainWallet. mainWalletId={}, userUuid={}",
                    mainWallet.getMainWalletId(), user.getUuid());

            SubWallet subWallet = validations.findSubWalletIfExists(mainWallet,
                    mainWalletRequest.getSubWalletId());
            log.debug(
                    "Found SubWallet. subWalletId={}, subWalletName={}, balance={}",
                    subWallet.getSubWalletId(), subWallet.getSubWalletName(),
                    subWallet.getBalance());

            if (subWallet.getBalance() == 0) {
                log.info(
                        "SubWallet [{}] has zero balance. Marking as deleted.",
                        subWallet.getSubWalletName());
                subWallet.setIsDeleted(true);
                mainWalletRepository.save(mainWallet);

                log.info(
                        "SubWallet [{}] successfully deleted (soft delete).",
                        subWallet.getSubWalletName());

                return MainWalletResponse.builder().message("SubWallet named ["
                                + subWallet.getSubWalletName()
                                + "] was deleted successfully as its balance was 0.")
                        .build();
            } else {
                log.error(
                        "Attempted to delete SubWallet [{}] with non-zero balance: {}",
                        subWallet.getSubWalletName(),
                        subWallet.getBalance());
                throw new CannotDeleteWalletException(
                        "Cannot delete SubWallet [" + subWallet.getSubWalletName()
                                + "] because it still contains a balance of "
                                + subWallet.getBalance()
                                + ". Please transfer or withdraw the funds first."
                );
            }
        } catch (IllegalArgumentException e) {
            throw new CannotDeleteWalletException(
                    "Invalid input: " + e.getMessage());
        }

    }
}
