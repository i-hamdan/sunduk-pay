
    package com.bxb.sunduk_pay.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

    /**
     * Convenience method to check whether a given path should be excluded.
     * Checks whether a given request path should be excluded
     * from authentication based on the configured list.
     *
     * @return true if the path should be excluded, false otherwise
     */

    @Data
    @Component
    @ConfigurationProperties(prefix = "sunduk.security")
    public class SundukSecurityProperties {
        /** List of
         *  paths to exclude from authentication. */
        private List<String> excludePaths;

    }

