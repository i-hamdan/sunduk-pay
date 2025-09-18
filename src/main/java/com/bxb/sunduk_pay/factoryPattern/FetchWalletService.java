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
 * Service to fetch main wallet and its active sub-wallets for a user.
 */
@Log4j2
@Service
public class FetchWalletService implements WalletOperation {

    private final Validations validations;
    private final WalletMapper walletMapper;

    public FetchWalletService(final Validations validations, final WalletMapper walletMapper) {
        this.validations = validations;
        this.walletMapper = walletMapper;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_WALLET;
    }

    /**
     * Fetches main wallet and non-deleted sub-wallets for a given user UUID.
     *
     * @param mainWalletRequest request containing the user UUID
     * @return response containing wallet and sub-wallet details
     */
    @Override
    public MainWalletResponse perform(final MainWalletRequest mainWalletRequest) {
        log.info("Fetching wallet for user UUID: {}", mainWalletRequest.getUuid());

        // Get main wallet
        final MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());

        // Filter active sub-wallets
        final List<SubWallet> activeSubWallets = mainWallet.getSubWallets().stream()
                .filter(subWallet -> !subWallet.getIsDeleted())
                .collect(Collectors.toList());

        log.debug("Found {} active sub-wallet(s) for user UUID: {}", activeSubWallets.size(), mainWalletRequest.getUuid());

        // Map to response
        return walletMapper.toWalletResponse(mainWallet, activeSubWallets);
    }
}
