package com.bxb.sunduk_pay.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
@Configuration
@ConfigurationProperties(prefix = "queries")
@Data
public class QueryConfig {
    private Map<String, Map<String,String>> modules;


}
