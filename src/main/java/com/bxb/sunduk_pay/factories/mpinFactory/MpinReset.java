package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.encryption.MpinEncryption;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MpinReset implements MpinOperation{

    /*** Repository for MPIN data. */
    private final MpinRepository mpinRepository;

    /*** Service for MPIN encryption. */
    private final MpinEncryption mpinEncryption;

    /*** MPIN record to be updated. */
    private final MpinValidations mpinValidations;
    @Override
    public MpinRequestType getMpinRequestType() {
        return MpinRequestType.RESET_MPIN;
    }

    @Override
    public MpinResponse perform(MpinRequest mpinRequest) {
        // Validate the UUID and retrieve the corresponding MPIN record.
        Mpin mpin = mpinValidations.findMpinByUuid(mpinRequest.getUuid());
       // Validate the existing MPIN.
        mpinValidations.validateMpin(mpinRequest.getUuid(),
                mpinRequest.getMpin());
        // Encrypt the new MPIN.
        String encryptMpin = mpinEncryption.encryptMpin(mpinRequest.getNewMpin());
        // Set the new MPIN for the user.
        mpin.setMpin(encryptMpin);
        mpin.setLockedUntil(null);
        mpin.setFailedAttempts(0);
        mpin.setLocked(false);
        // Save the updated MPIN record.
         mpinRepository.save(mpin);
        return MpinResponse.builder()
                .title("MPIN Change successfully.")
                .message(
                        " You can now  use your new MPIN to access your " +
                        "account and authorize transactions.")
                .build();

    }
}
