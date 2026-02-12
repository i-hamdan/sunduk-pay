package com.bxb.sunduk_pay.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Configuration class for Kafka error handling.
 * Sets up a global error handler that publishes failed messages
 * to a Dead Letter Topic (DLT) with retry logic.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * Configures a global error handler for Kafka listeners.
     * Failed messages will be published to a DLT with a retry mechanism.
     *
     * @param kafkaTemplate the Kafka template used for publishing to DLT
     * @return the configured DefaultErrorHandler
     */
    @Bean
    public DefaultErrorHandler globalErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (
                record, ex) -> new TopicPartition(
                                record.topic() + ".DLT",
                                record.partition()
                        ));

        FixedBackOff backOff = new FixedBackOff(2000L, 3L);
        // 2 seconds delay, 3 retries max

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        return errorHandler;
    }
}
