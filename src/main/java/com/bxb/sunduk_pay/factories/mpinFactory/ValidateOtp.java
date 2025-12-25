package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.exception.InvalidMpinException;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateOtp implements MpinOperation{
    /**
     * Cache to store OTPs temporarily.
     */
    private final Cache<String, String> otpCache;

    /**
     * Returns the type of MPIN request this operation handles.
     * @return MpinRequestType associated with this operation.
     */
    @Override
    public MpinRequestType getMpinRequestType() {
        return MpinRequestType.VALIDATE_OTP;
    }

    /**
     * Performs the OTP validation operation
     * @param mpinRequest the request containing
     *                          necessary data for the operation.
     * @return MpinResponse containing the result of the operation.
     */
    @Override
    public MpinResponse perform(final MpinRequest mpinRequest) {
     // retrieve saved otp email cache
        String savedOtp = otpCache.getIfPresent(mpinRequest.getEmail());
        // validate otp
        if (savedOtp != null && savedOtp.equals(mpinRequest.getOtp())) {
            // invalidate otp after successful validation
            otpCache.invalidate(mpinRequest.getEmail());

            return MpinResponse.builder()
                    .message("OTP validation successful.")
                    .build();
        } else {
            // throw exception for invalid otp
            throw new InvalidMpinException("Invalid code.please check your" +
                    " email for the correct code " +
                    "and re entre.");
        }
    }
}
