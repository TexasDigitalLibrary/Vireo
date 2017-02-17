package org.tdl.vireo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import org.tdl.vireo.controller.interceptor.AppStompInterceptor;
import edu.tamu.framework.config.CoreWebSocketConfig;

@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends CoreWebSocketConfig {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(appStompInterceptor());
    }

    @Bean
    public AppStompInterceptor appStompInterceptor() {
        return new AppStompInterceptor();
    }

}

