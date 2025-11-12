package com.bxb.sunduk_pay.config;

import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.ChatAndTransactionUnifiedDTO;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for RedisTemplate with custom serializers.
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@RequiredArgsConstructor
@Log4j2
public class RedisConfig {

    /**
     * Redis connection factory.
     */
private final RedisConnectionFactory connectionFactory;

    /**
     * Initializes the Redis connection and logs the status.
     */
    @PostConstruct
    public void init() {
        try {
            String pong = connectionFactory.getConnection().ping();
            log.info("Redis connected successfully: {}", pong);
        } catch (Exception e) {
            log.error(
         "Redis connection failed during initialization: {}",
                    e.getMessage(), e);
        }
    }

    /**
     * Configures and returns a RedisTemplate with
     * custom key and value serializers.
     *
     * @param connectionFactory the RedisConnectionFactory to use
     * @return a configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use the customized ObjectMapper in serializer
        GenericJackson2JsonRedisSerializer serializer = new
                GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


    /**
     * Configures and returns a RedisTemplate specifically
     * for ChatMessage objects with custom serializers.
     *
     * @param connectionFactory the RedisConnectionFactory to use
     * @return a configured RedisTemplate for ChatMessage
     */
    @Bean
    public RedisTemplate<String, ChatMessage> chatMessageRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper for LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Pass ObjectMapper in constructor (no deprecated setObjectMapper)
        Jackson2JsonRedisSerializer<ChatMessage> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ChatMessage.class);

        // Set serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, TransactionResponse> chatTransactionRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, TransactionResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper for LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Pass ObjectMapper in constructor (no deprecated setObjectMapper)
        Jackson2JsonRedisSerializer<TransactionResponse> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, TransactionResponse.class);

        // Set serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }


}
