package com.tictactoe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broadcast and user-specific queues prefix
        config.enableSimpleBroker("/topic", "/queue");
        
        // Incoming app messages prefix destination
        config.setApplicationDestinationPrefixes("/app");
        
        // User queue prefix destination
        config.setUserDestinationPrefix("/user");
    }

    @org.springframework.beans.factory.annotation.Value("${allowed.origins:*}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register WebSocket connection endpoint for SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .withSockJS();
    }
}
