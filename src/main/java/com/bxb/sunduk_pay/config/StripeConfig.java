package com.bxb.sunduk_pay.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Central place for Stripe credentials. This sets the global Stripe API key once at startup.
 * Central place for Stripe credentials.
 * This sets the global Stripe API key once at startup.
 */
@Configuration
@Data
public class StripeConfig {
    @Value("${stripe.key.secret}")
    private String secretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}