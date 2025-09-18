package com.bxb.sunduk_pay.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Central place for Stripe credentials.
 * This sets the global Stripe API key once at startup.
 */
@Configuration
@Getter // only getters are normally needed for config values
public class StripeConfig {

    /**
     * Secret API key used for server-side calls to Stripe.
     */
    @Value("${stripe.key.secret}")
    private String secretKey;

    /**
     * Secret used to validate incoming webhook signatures.
     */
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /**
     * Configure Stripe once the bean is created.
     */
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                    "Stripe secret key must be configured!"
            );
        }
        Stripe.apiKey = secretKey;
    }
}
