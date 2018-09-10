package org.tdl.vireo.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import edu.tamu.weaver.token.service.TokenService;
import io.jsonwebtoken.Claims;

@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenService tokenService;

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
        registration.taskExecutor().corePoolSize(16).maxPoolSize(Integer.MAX_VALUE);
        registration.setInterceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwt = accessor.getFirstNativeHeader("jwt");
                    if (!StringUtils.isEmpty(jwt)) {
                        try {
                            Claims claims = tokenService.parseIgnoringExpiration(jwt);
                            accessor.setUser(new UsernamePasswordAuthenticationToken(claims.getSubject(), claims));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return message;
            }
        });
    }

}
