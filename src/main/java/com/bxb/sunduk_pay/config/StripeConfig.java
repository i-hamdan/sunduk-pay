package com.bxb.sunduk_pay.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Central place for Stripe credentials.
 * This sets the global Stripe API key once at startup.
 * Central place for Stripe credentials.
 * This sets the global Stripe API key once at startup.
 */
@Configuration
@Data
public class StripeConfig {
    /** The secret API key used for authenticating
     * requests to Stripe.
     * This value is injected from application properties.
     */
    @Value("${stripe.key.secret}")
    private String secretKey;
    /***
     * The webhook secret used to verify incoming Stripe webhook events.
     * This value is injected from application properties.
      */
@Value("${stripe.webhook.secret}")
    private String webhookSecret;
/** * Initializes the Stripe API key
 *  after the bean is constructed.
 * This method is called automatically
 * by Spring after dependency injection is done.
 */
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}
