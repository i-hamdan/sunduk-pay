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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class CreateService implements WalletOperation {
private final Validations validations;
private final MainWalletRepository mainWalletRepository;
    public CreateService(Validations validations, MainWalletRepository mainWalletRepository) {
        this.validations = validations;
        this.mainWalletRepository = mainWalletRepository;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE;
    }

    @Transactional
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

        validations.getUserInfo(mainWalletRequest.getUuid());
        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());

        List<SubWallet> subWallets = mainWallet.getSubWallets();
        int size = subWallets.size();
validations.validateNumberOfSubWallets(size);
            SubWallet subWallet = SubWallet.builder()
                    .subWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .targetBalance(mainWalletRequest.getTargetBalance())
                    .subWalletName(mainWalletRequest.getSubWalletName())
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            subWallets.add(subWallet);
            mainWallet.setSubWallets(subWallets);

            mainWalletRepository.save(mainWallet);

          return MainWalletResponse.builder().message("Sub wallet created successfully").build();

    }

}
