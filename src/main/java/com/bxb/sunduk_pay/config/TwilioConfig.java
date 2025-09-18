package com.bxb.sunduk_pay.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Holds Twilio configuration properties from application.yml.
 *
 * twilio:
 *   account-sid: your-account-sid
 *   auth-token: your-auth-token
 *   from-number: +1234567890
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {

    /**
     * Twilio Account SID.
     */
    private String accountSid;

    /**
     * Twilio Auth Token.
     */
    private String authToken;

    /**
     * Default sender phone number for messages.
     */
    private String fromNumber;
}
