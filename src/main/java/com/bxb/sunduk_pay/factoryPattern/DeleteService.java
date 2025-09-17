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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class DeleteService implements WalletOperation {
    private final Validations validations;
    private final MainWalletRepository mainWalletRepository;

    public DeleteService(Validations validations, MainWalletRepository mainWalletRepository) {
        this.validations = validations;
        this.mainWalletRepository = mainWalletRepository;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        log.info("Received request to delete SubWallet.");

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        log.debug("Fetched user info. userUuid={}, userName={}", user.getUuid(), user.getFullName());

        validations.getMainWalletInfo(user.getUuid());
        log.debug("Validated MainWallet info for userUuid={}", user.getUuid());

        MainWallet mainWallet = validations.getMainWalletByWalletId(mainWalletRequest.getMainWalletId());
        log.debug("Fetched MainWallet. mainWalletId={}, userUuid={}", mainWallet.getMainWalletId(), user.getUuid());

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getSubWalletId());
        log.debug("Found SubWallet. subWalletId={}, subWalletName={}, balance={}",
                subWallet.getSubWalletId(), subWallet.getSubWalletName(), subWallet.getBalance());

        if (subWallet.getBalance() == 0) {
            log.info("SubWallet [{}] has zero balance. Marking as deleted.", subWallet.getSubWalletName());
            subWallet.setIsDeleted(true);
            mainWalletRepository.save(mainWallet);

            log.info("SubWallet [{}] successfully deleted (soft delete).", subWallet.getSubWalletName());

            return MainWalletResponse.builder().message("SubWallet named [" + subWallet.getSubWalletName() + "] was deleted successfully as its balance was 0.")
                    .build();

        } else {
            log.error("Attempted to delete SubWallet [{}] with non-zero balance: {}",
                    subWallet.getSubWalletName(), subWallet.getBalance());
            throw new CannotDeleteWalletException(
                    "Cannot delete SubWallet [" + subWallet.getSubWalletName() + "] because it still contains a balance of " + subWallet.getBalance() + ". Please transfer or withdraw the funds first."
            );
        }

    }
}
