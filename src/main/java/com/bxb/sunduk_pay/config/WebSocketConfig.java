package com.bxb.sunduk_pay.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * Configuration class for WebSocket with STOMP support.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers STOMP endpoints for WebSocket connections.
     * @param registry
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOrigins("*")
            .withSockJS();
    }

    /**
     * Configures the message broker for handling messages.
     * @param registry
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
