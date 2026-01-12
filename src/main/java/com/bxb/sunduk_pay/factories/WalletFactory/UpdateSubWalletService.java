package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.exception.CannotUpdateWalletException;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.UpdateWalletActionType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service to handle updates to SubWallets.
 * Supports renaming,
 * updating goal amounts,
 * and changing target dates.
 * Validates user and wallet info before performing updates.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateSubWalletService implements WalletOperation {
    /**
     * Repository for accessing MainWallet data.
     */
    private final MainWalletRepository mainWalletRepository;

    /**
     * Validations class for various validations.
     */
    private final Validations validations;

    /**
     * Returns the RequestType handled by this service.
     *
     * @return RequestType.UPDATE
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE;
    }

    /**
     * Updates a SubWallet based on the action
     * type specified in the request.
     * Supports renaming, updating goal amounts,
     * and changing target dates.
     *
     * @param mainWalletRequest request containing user UUID, wallet ID,
     *  action type, and relevant update details
     * @return response indicating success or failure of the update
     * @throws ResourceNotFoundException if user or wallet is not found
     * @throws CannotUpdateWalletException if the
     * @throws InvalidPayloadException if the request payload is invalid
     */
    @Override
    public MainWalletResponse perform(
            final MainWalletRequest mainWalletRequest) {
        log.info("Performing action [{}]",
                mainWalletRequest.getActionType());

        // validating User
        User user = validations.getUserInfo(
                mainWalletRequest.getUuid());
        log.debug("Fetched user details for UUID: {}",
                user.getUuid());

        // validation mainwallet info
        MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
        log.debug("Validated main wallet info for user UUID: {}",
                user.getUuid());

        // find all active pots
        SubWallet subWallet = validations.findSubWalletIfExists(
                mainWallet.getMainWalletId(),
                mainWalletRequest.getSubWalletId());

        // checking subwallets name
        String oldName = (subWallet != null)
                ? subWallet.getSubWalletName() : null;
        Double oldTargetBalance = (subWallet != null)
                ? subWallet.getTargetBalance() : null;
        LocalDate oldTargetDate = (subWallet != null)
                ? subWallet.getTargetDate() : null;

        if (mainWalletRequest.getActionType()
                == UpdateWalletActionType.GOAL_AMOUNT) {
            if (subWallet != null) {
                log.info(
           "Updating goal amount of SubWallet [{}] under MainWallet [{}]",
                        subWallet.getSubWalletId(),
                        mainWallet.getMainWalletId());
                if (mainWalletRequest.getTargetBalance() == null
                        || mainWalletRequest.getTargetBalance() == 0) {
                    throw new InvalidPayloadException(
                            "Target balance cannot be null or zero.");
                }
               subWallet.setTargetBalance(mainWalletRequest.getTargetBalance());
                subWallet.setUpdatedAt(LocalDateTime.now());
                mainWalletRepository.save(mainWallet);
                log.info(
           "SubWallet [{}] goal amount successfully updated from [{}] to [{}]",
                        subWallet.getSubWalletId(),
                        oldTargetBalance,
                        subWallet.getTargetBalance()
                );
                return MainWalletResponse.builder()
                        .message(
                          subWallet.getSubWalletName()
                          + "'s target balance was successfully updated to "
                          + mainWalletRequest.getTargetBalance() + "."
                        ).build();
            } else {
                log.error(
        "Update failed! unable to find subWallet with Id [{}] ",
             mainWalletRequest.getSubWalletId());
                throw new CannotUpdateWalletException(
             "Cannot update subWallet! Unable to find subWallet with Id: "
                 + mainWalletRequest.getSubWalletId()
                );
            }
        }
        if (mainWalletRequest.getActionType()
                == UpdateWalletActionType.GOAL_DATE) {
            if (subWallet != null) {
                log.info(
               "Updating goal date of SubWallet [{}] under MainWallet [{}]",
                        subWallet.getSubWalletId(),
                        mainWallet.getMainWalletId());

//                DateTimeFormatter.ofPattern("");
                subWallet.setTargetDate(mainWalletRequest.getTargetDate());
                subWallet.setUpdatedAt(LocalDateTime.now());
                mainWalletRepository.save(mainWallet);
                log.info(
           "SubWallet [{}] goal date was updated from [{}] to [{}]",
                        subWallet.getSubWalletId(),
                        oldTargetDate,
                        subWallet.getTargetDate());
                return MainWalletResponse.builder()
                   .message(subWallet.getSubWalletName()
                  + "'s target date was successfully updated to "
                + mainWalletRequest.getTargetDate() + ".").build();
            } else {
                log.error(
        "Update failed! unable to find subWallet with Id [{}] ",
                        mainWalletRequest.getSubWalletId());
                throw new CannotUpdateWalletException(
                        " Cannot find subWallet with Id : "
                                + mainWalletRequest.getSubWalletId()
                                + " ! SubWallet Id might be invalid. ");
            }
        }
        log.error("Invalid ActionType [{}] provided in request",
                mainWalletRequest.getActionType());
        throw new InvalidPayloadException("Please provide a valid ActionType!");
    }
}
