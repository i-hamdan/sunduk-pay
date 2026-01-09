package com.bxb.sunduk_pay.config;

import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for RedisTemplate with custom serializers.
 */
@Configuration
@RequiredArgsConstructor
@Log4j2
public class RedisConfig {

    /**
     * Redis server host.
     */
    @Value("${spring.redis.host}")
    private String redisHost;

    /**
     * Redis server port.
     */
    @Value("${spring.redis.port}")
    private int redisPort;

    /**
     * Manually configure the Redis connection factory so it doesn't
     * default to localhost.
     *  @return LettuceConnectionFactory for Redis connections
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info(
                "Creating RedisConnectionFactory with host={} and port={}",
                redisHost, redisPort);
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisHost, redisPort));
    }

    //  Initialize after full context is ready (safe & no circular refs)
    /**
     * Verifies the Redis connection upon application context refresh.
     *
     * @param event the context refreshed event
     */
    @EventListener(ContextRefreshedEvent.class)
    public void verifyRedisConnection(final ContextRefreshedEvent event) {
        try {
            RedisConnectionFactory factory = event
                    .getApplicationContext().getBean(
                            RedisConnectionFactory.class);
            String pong = factory.getConnection().ping();
            log.info(" Redis connected successfully: {}", pong);
        } catch (Exception e) {
            log.error(
                    " Redis connection failed during initialization: {}",
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
            final RedisConnectionFactory connectionFactory) {
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
            final RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper for LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Pass ObjectMapper in constructor (no deprecated setObjectMapper)
        Jackson2JsonRedisSerializer<ChatMessage> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper,
                        ChatMessage.class);

        // Set serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * Configures and returns a RedisTemplate specifically
     * for TransactionResponse objects with custom serializers.
     *
     * @param connectionFactory the RedisConnectionFactory to use
     * @return a configured RedisTemplate for TransactionResponse
     */
    @Bean
    public RedisTemplate<String, TransactionResponse>
    chatTransactionRedisTemplate(
            final RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, TransactionResponse> template =
                new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper for LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Pass ObjectMapper in constructor (no deprecated setObjectMapper)
        Jackson2JsonRedisSerializer<TransactionResponse> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper,
                        TransactionResponse.class);

        // Set serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * Configures and returns a RedisTemplate specifically
     * for GlobalPotResponse objects with custom serializers.
     *
     * @param connectionFactory the RedisConnectionFactory to use
     * @return a configured RedisTemplate for GlobalPotResponse
     */
    @Bean
    public RedisTemplate<String, GlobalPotResponse>
    globalPotResponseRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, GlobalPotResponse> template =
                new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<GlobalPotResponse> serializer
                = new Jackson2JsonRedisSerializer<>(
                objectMapper, GlobalPotResponse.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


    /**
     * Configures and returns a RedisTemplate specifically
     * for GroupChatMessageResponse objects with custom serializers.
     *
     * @param connectionFactory the RedisConnectionFactory to use
     * @return a configured RedisTemplate for GroupChatMessageResponse
     */
    @Bean
    public RedisTemplate<String,
            GroupChatMessageResponse> groupChatResponseRedisTemplate(
                    final RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, GroupChatMessageResponse>
                template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<GroupChatMessageResponse> serializer
                = new Jackson2JsonRedisSerializer<>(
                objectMapper, GroupChatMessageResponse.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

}
