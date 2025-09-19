package com.bxb.sunduk_pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Holds Twilio configuration properties from application.yml:
 * Holds Twilio configuration properties from application.yml.
 *
 * twilio:
 *   account-sid: your-account-sid
 *   auth-token: your-auth-token
 *   from-number: +1234567890
 */
@Component
@Data
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {
    private String accountSid;
    private String authToken;
    private String fromNumber;
}
