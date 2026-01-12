package com.bxb.sunduk_pay.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/* Cache configuration class for managing OTP caching. */
@Configuration
public class CacheConfig {
    /* Cache ka maximum size define kar rahe hain */
    private static final int MAX_CACHE_SIZE = 1000;
    /* OTP expiry time define kar rahe hain (5 minutes) */
    private static final int OTP_EXPIRY_MINUTES = 5;

    /* OTP cache bean define kar rahe hain */
    @Bean
    public Cache<String, String> otpCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(OTP_EXPIRY_MINUTES, TimeUnit.MINUTES)
                .maximumSize(MAX_CACHE_SIZE)
                .build();
    }
}
