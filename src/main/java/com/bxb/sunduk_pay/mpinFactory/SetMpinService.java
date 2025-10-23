package com.bxb.sunduk_pay.mpinFactory;

import com.bxb.sunduk_pay.encryption.MpinEncryption;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Log4j2
@Service
public class SetMpinService implements MpinOperation{
    /** Service for MPIN encryption. */
    private final MpinEncryption mpinEncryption;
    /** Utility for validations. */
    private  final Validations validations;
    /** Repository for MPIN persistence. */
    private final MpinRepository repository;
    /**
     * Returns the MpinRequestType handled by this service.
     *
     * @return MpinRequestType.SET_MPIN
     */
    @Override
    public MpinRequestType getMpinRequestType() {
        return MpinRequestType.SET_MPIN;
    }
    /**
     * Sets a new MPIN for the user.
     *
     * @param mpinRequest request containing user UUID and new MPIN
     * @return response indicating success
     */
    @Override
    public MpinResponse perform(MpinRequest mpinRequest) {
        String encryptMpin = mpinEncryption.encryptMpin(mpinRequest.getMpin());

        User user = validations.
                getUserInfo(mpinRequest.getUuid());
        log.info("Setting MPIN for user UUID: {}",
                user.getUuid());

        Mpin mpin = Mpin.builder()
                .user(user)
                .mpin(encryptMpin)
                .build();

        repository.save(mpin);

        return MpinResponse.builder()
                .message("MPIN set successfully." +
                        " You can now use your new MPIN " +
                        "to access your account and authorize transaction.")
                .build();
    }
}
