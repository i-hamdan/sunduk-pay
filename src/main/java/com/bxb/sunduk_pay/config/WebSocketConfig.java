package com.bxb.sunduk_pay.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket with STOMP support.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Log4j2
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


//    /**
//     * Interceptor for chat messages.
//     */
//    private final ChatMessageInterceptor chatMessageInterceptor;

//    /**
//     * Configures the client inbound channel with interceptors.
//     *
//     * @param registration
//     */
//    @Override
//    public void configureClientInboundChannel(
//    ChannelRegistration registration) {
//        registration.interceptors(chatMessageInterceptor);
//    }

    /**
     * Registers STOMP endpoints for WebSocket connections.
     *
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    /**
     * Configures the message broker for handling messages.
     *
     * @param registry the message broker registry
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

}

