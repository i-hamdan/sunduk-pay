package com.bxb.sunduk_pay.factories.photoFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration class for asynchronous execution.
 * Provides a dedicated thread pool for photo upload tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Executor bean used for photo upload background tasks.
     *
     * @return Executor thread pool
     */
    @Bean("photoExecutor")
    public Executor photoExecutor() {
        return Executors.newFixedThreadPool(32);
    }
}

