package org.tdl.vireo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.mock.MockMessageChannel;

@Configuration
@Profile(value = { "test" })
public class TestConfig {

    @Bean
    @Primary
    public SimpMessagingTemplate simpMessagingTemplate() {
        return new SimpMessagingTemplate(new MockMessageChannel());
    }

}
