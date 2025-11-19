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

@RequiredArgsConstructor
@Service
public class FogetMpinReset implements MpinOperation{
    /**
     * Utility for validations.
     */
    private final MpinValidations validations;
    /** Service for MPIN encryption. */
   private final MpinEncryption mpinEncryption;
    /**
     * Repository for MPIN data.
     */

   private final MpinRepository mpinRepository;

    /**
     * Returns the type of MPIN request this operation handles.
     * @return  MpinRequestType associated with this operation.
     */
    @Override
    public MpinRequestType getMpinRequestType() {
        return MpinRequestType.FORGOT_MPIN_RESET;
    }
    /**
     * Performs the MPIN reset operation based on the provided request.
     * @param mpinRequest the request containing necessary data for the operation.
     * @return MpinResponse containing the result of the operation.
     */

    @Override
    public MpinResponse perform(MpinRequest mpinRequest) {
        //Validate the UUID and retrieve the corresponding MPIN record.
        Mpin mpin = validations.findMpinByUuid(mpinRequest.getUuid());
        // Encrypt the new MPIN.
        String encryptMpin =
                mpinEncryption.encryptMpin(mpinRequest.getNewMpin());
        // set new mpin for user
       mpin.setMpin(encryptMpin);
       mpin.setLockedUntil(null);
       mpin.setFailedAttempts(0);
       mpin.setLocked(false);

       mpinRepository.save(mpin);
        return MpinResponse.builder()
                .title("MPIN reset successfully.")
                .message(
                        " You can use it now to access your account" +
                        "and authorize transactions.")
                .build();
    }
}
