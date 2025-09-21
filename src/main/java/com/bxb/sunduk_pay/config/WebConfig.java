package com.bxb.sunduk_pay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the application.
 * This applies to controllers that are not covered by Spring Security CORS rules.
 * Applies to controllers that are not covered by
 * Spring Security CORS rules.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/")
                .allowedOrigins("http://192.168.29.112:19006", "http://192.168.29.112:8081",
                        "https://1a617a399309.ngrok-free.app")// IP  Expo bundler URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}