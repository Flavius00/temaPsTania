// app backend/src/main/java/com/example/demo/config/WebSocketConfig.java
package com.example.demo.config;

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
        // Enable a simple memory-based message broker to carry messages back to the client
        // The destination prefixes handled by the broker are /topic and /queue
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from clients to the application
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint, enabling SockJS fallback options for clients
        // that don't support WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000") // Allow React frontend
                .withSockJS();
    }
}