package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.exception.InvalidMpinException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.repository.MpinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class MpinValidationImpl implements MpinValidations {
    /**
     * Repository for MPIN operations.
     */
    private final MpinRepository mpinRepository;
    /**
     * Service for MPIN encryption and verification.
     */
    private final MpinEncryption encryption;


    @Override
    public void validateMpinForPayment(final String uuid, final String inputMpin) {
        log.info("Validating MPIN for user: {}", uuid);

        Mpin mpin = mpinRepository.findByUser_Uuid(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException
                                ("MPIN not found for user: " + uuid));


        String storedMpin = mpin.getMpin();

        boolean isValid = encryption.verifyMpin(inputMpin, storedMpin);

        if (!isValid) {
            log.error("MPIN validation failed for user: {}", uuid);
            throw new InvalidMpinException("Incorrect MPIN entered.");
        }

        log.info("MPIN validation successful for user: {}", uuid);
    }

    /**
     * Validates the MPIN for setting a new MPIN.
     *
     * @param uuid
     */
    @Override
    public Mpin findMpinByUuid(String uuid) {
        return mpinRepository.findByUser_Uuid(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException
                                ("MPIN not found for user: " + uuid));
    }

    /**
     * Validates the MPIN for reset operations.
     * @param uuid
     * @param inputMpin
     */
    @Override
    public void validateMpinForReset(String uuid, String inputMpin) {
        log.info("Validating MPIN for user: {}", uuid);
        Mpin mpin = mpinRepository.findByUser_Uuid(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException
                                ("MPIN not found for user: " + uuid));


        String storedMpin = mpin.getMpin();

        boolean isValid = encryption.verifyMpin(inputMpin, storedMpin);

        if (!isValid) {
            log.error("MPIN validation failed for user: {}", uuid);
            throw new InvalidMpinException("Incorrect MPIN entered.");
        }

    }

}
