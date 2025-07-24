
    package com.bxb.sunduk_pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
    @Data
    @Component
    @ConfigurationProperties(prefix = "sunduk.security")
    public class SundukSecurityProperties {
        private List<String> excludePaths;

    }
/*
 * This configuration class is used to bind custom security-related properties
 * defined under 'sunduk.security' in the application.yml file.

 * For example, 'exclude-paths' in the YAML is a List of Strings, which cannot
 * be directly injected using @Value without converting the list into a comma-separated string.
 *
 * @ConfigurationProperties automatically maps such YAML structures into Java objects.
 */


