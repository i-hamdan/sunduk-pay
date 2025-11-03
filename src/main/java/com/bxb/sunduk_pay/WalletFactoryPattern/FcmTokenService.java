package com.bxb.sunduk_pay.WalletFactoryPattern;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@AllArgsConstructor
@Slf4j
/**
 * Service class for updating FCM tokens for users.
 */
public class FcmTokenService implements WalletOperation {

    /** Validations utility for input validation and data retrieval. **/
    private final Validations validations;

    /** Repository for User entity operations. **/
    private final UserRepository userRepository;
    /**
     * @return RequestType.UPADTE_FCM_TOKEN request type.
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_FCM_TOKEN;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

        User user=validations.getUserInfo(mainWalletRequest.getUuid());
        user.setFcmToken(mainWalletRequest.fcmToken);
        userRepository.save(user);
        log.info("FCM Token updated for user: {}", mainWalletRequest.getUuid());

        MainWalletResponse mainWalletResponse= MainWalletResponse.builder()
                .message("FCM Token Updated Successfully")
                .build();
        return mainWalletResponse;
    }
}
