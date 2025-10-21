package com.bxb.sunduk_pay.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class to load query
 * templates from application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "queries")
@Data
public class QueryConfig {
    /**
     * Map of modules containing query templates.
     */
    private Map<String, Map<String,String>> modules;

    /**
     * Initializes the configuration after properties are set.
     */
    @PostConstruct
    public void init() {
        // This method will be called after the properties are set
        System.out.println("Loaded query configurations: " + modules);
    }

}
