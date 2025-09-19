package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to fetch the main wallet and active sub-wallets for a user.
 */
@Log4j2
@Service
public final class FetchWalletService implements WalletOperation {

    /** Validations utility for user and wallet checks. */
    private final Validations validations;

    /** Mapper to convert wallet entities to response DTOs. */
    private final WalletMapper walletMapper;

    /**
     * Constructor for FetchWalletService.
     *
     * @param validations validations utility
     * @param walletMapper wallet mapper
     */
    public FetchWalletService(final Validations validations,
                              final WalletMapper walletMapper) {
        this.validations = validations;
        this.walletMapper = walletMapper;
    }

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
     * Fetches main wallet and active (non-deleted) sub-wallets for a given user UUID.
     *
     * @param mainWalletRequest request containing the user UUID
     * @return response containing wallet and sub-wallet details
     */
    @Override
    public MainWalletResponse perform(final MainWalletRequest mainWalletRequest) {
        log.info("Fetching wallet for user UUID: {}", mainWalletRequest.getUuid());

        // Fetch main wallet
        final MainWallet mainWallet = validations.getMainWalletInfo(
                mainWalletRequest.getUuid()
        );

        // Filter active sub-wallets
        final List<SubWallet> activeSubWallets = mainWallet.getSubWallets().stream()
                .filter(subWallet -> !subWallet.getIsDeleted())
                .collect(Collectors.toList());

        log.debug("Found {} active sub-wallet(s) for user UUID: {}",
                activeSubWallets.size(),
                mainWalletRequest.getUuid()
        );

        // Map main wallet and active sub-wallets to response DTO
        return walletMapper.toWalletResponse(mainWallet, activeSubWallets);
    }
}
