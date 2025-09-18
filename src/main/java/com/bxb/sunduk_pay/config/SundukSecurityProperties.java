package com.bxb.sunduk_pay.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Configuration class for security-related properties defined under
 * 'sunduk.security' in application.yml.
 *
 * Example YAML:
 * sunduk:
 *   security:
 *     exclude-paths:
 *       - /login
 *       - /public/**
 *
 * The excludePaths list can then be injected wherever needed.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sunduk.security")
public class SundukSecurityProperties {

    /**
     * List of endpoint paths to be excluded from authentication checks.
     */
    private List<String> excludePaths = Collections.emptyList();

    /**
     * Convenience method to check whether a given path should be excluded.
     */
    public boolean isExcluded(String path) {
        return excludePaths != null && excludePaths.stream().anyMatch(path::startsWith);
    }
}
