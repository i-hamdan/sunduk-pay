package com.bxb.sunduk_pay.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, String> otpCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // OTP 5 min me expire ho jayega
                .maximumSize(1000)
                .build();
    }
}
