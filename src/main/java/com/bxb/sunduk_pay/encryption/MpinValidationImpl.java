package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.exception.InvalidMpinException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.repository.MpinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

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

    /**
     * Validates the MPIN for payment operations.
     * @param uuid
     * @param inputMpin
     */
    @Override
    public void validateMpin(final String uuid, final String inputMpin) {
        log.info("Validating MPIN for user: {}", uuid);

        Mpin mpin = mpinRepository.findByUser_Uuid(uuid)
                .orElseThrow(() ->
                        new UserNotFoundException("MPIN not found for user: " + uuid));

        // Check if locked
        if (mpin.isLocked()) {
            if (mpin.getLockedUntil() != null && mpin.getLockedUntil()
                    .isBefore(LocalDateTime.now())) {

                log.info("MPIN auto-unlocked for user: {}", uuid);
                mpin.setLocked(false);
                mpin.setFailedAttempts(0);
                mpin.setLockedUntil(null);
                mpinRepository.save(mpin);
            } else {
                Duration remaining = Duration.between(LocalDateTime.now(),
                        mpin.getLockedUntil());
                long hoursLeft = remaining.toHours();
                log.info(hoursLeft);
                long minutesLeft = remaining.toMinutesPart();
                log.info(minutesLeft);
                long secondLeft = remaining.toSeconds();
                log.info(secondLeft);
                throw new InvalidMpinException(
                      "Your MPIN is blocked. Try again in "+ hoursLeft +
                                minutesLeft + secondLeft);

            }
        }

        // Verify MPIN
        String storedMpin = mpin.getMpin();
        boolean isValid = encryption.verifyMpin(inputMpin, storedMpin);

        if (!isValid) {
            int failedAttempts = mpin.getFailedAttempts() + 1;
            mpin.setFailedAttempts(failedAttempts);


            if (failedAttempts >= 3) {
                mpin.setLocked(true);
                mpin.setLockedUntil(LocalDateTime.now().plusMinutes(10));
                mpinRepository.save(mpin);

                log.error("User {} MPIN blocked for 2 minutes after 3" +
                        " failed attempts", uuid);
                
                throw new InvalidMpinException(

                                "You have entered the wrong MPIN too many times. " +
                                        "Your account is temporary locked for" +
                                        " 24 hours");
            }

            // Save updated failed attempts before throwing
            mpinRepository.save(mpin);


            int attemptsLeft = 3 - failedAttempts;
            throw new InvalidMpinException("you have entered a incorrect mpin "
                     + attemptsLeft + " attempt  remaining");
        }

        // If valid, reset failed attempts
        mpin.setFailedAttempts(0);
        mpinRepository.save(mpin);

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
}

