package org.tdl.vireo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/channel");
        registry.setApplicationDestinationPrefixes("/ws");
        registry.setUserDestinationPrefix("/private");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect").setAllowedOrigins("*").withSockJS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(Integer.MAX_VALUE);
        registration.setSendBufferSizeLimit(Integer.MAX_VALUE);
        registration.setSendTimeLimit(2 * 10 * 10000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(8).maxPoolSize(Integer.MAX_VALUE);
    }

}
