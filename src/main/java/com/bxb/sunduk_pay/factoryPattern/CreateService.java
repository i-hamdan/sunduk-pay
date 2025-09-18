package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service to handle the creation of SubWallets.
 * Validates user and main wallet, checks existing sub-wallet count, then adds a new SubWallet.
 */
@Log4j2
@Service
public class CreateService implements WalletOperation {

    private final Validations validations;
    private final MainWalletRepository mainWalletRepository;

    public CreateService(final Validations validations,
                         final MainWalletRepository mainWalletRepository) {
        this.validations = validations;
        this.mainWalletRepository = mainWalletRepository;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE;
    }

    /**
     * Creates a new sub-wallet for a user.
     *
     * @param mainWalletRequest request containing user UUID and sub-wallet details
     * @return response indicating success
     */
    @Override
    public MainWalletResponse perform(final MainWalletRequest mainWalletRequest) {
        log.info("Starting SubWallet creation for User UUID: {}", mainWalletRequest.getUuid());

        try {
            // Validate user
            validations.getUserInfo(mainWalletRequest.getUuid());
            log.debug("User validation successful for UUID: {}", mainWalletRequest.getUuid());

            // Fetch main wallet
            final MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());
            log.debug("MainWallet fetched successfully for UUID: {}", mainWalletRequest.getUuid());

            // Filter non-deleted sub-wallets
            final List<SubWallet> subWallets = mainWallet.getSubWallets().stream()
                    .filter(sw -> !sw.getIsDeleted())
                    .collect(Collectors.toList());

            // Validate number of sub-wallets
            final int size = subWallets.size();
            validations.validateNumberOfSubWallets(size);
            log.debug("SubWallet count validation passed. Current size: {}", size);

            // Build new sub-wallet
            final SubWallet subWallet = SubWallet.builder()
                    .subWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .targetBalance(mainWalletRequest.getTargetBalance())
                    .targetDate(mainWalletRequest.getTargetDate())
                    .subWalletName(mainWalletRequest.getSubWalletName())
                    .isDeleted(false)
                    .icon(mainWalletRequest.getIcon())
                    .createdAt(LocalDateTime.now())
                    .build();

            log.info("New SubWallet built with name={} and targetBalance={}",
                    subWallet.getSubWalletName(), subWallet.getTargetBalance());

            // Save to main wallet
            mainWallet.getSubWallets().add(subWallet);
            mainWalletRepository.save(mainWallet);
            log.info("SubWallet saved successfully for User UUID: {}", mainWalletRequest.getUuid());

            return MainWalletResponse.builder()
                    .message("Sub wallet created successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to create SubWallet for User UUID: {}. Reason: {}",
                    mainWalletRequest.getUuid(), e.getMessage());
            throw e;
        }
    }
}
