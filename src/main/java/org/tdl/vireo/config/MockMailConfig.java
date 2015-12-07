package org.tdl.vireo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tdl.vireo.service.EmailService;
import org.tdl.vireo.service.MockEmailServiceImpl;

@Configuration
@Profile(value = { "test" })
public class MockMailConfig extends MailConfig {

    @Override
    @Bean
    public EmailService emailService() {
        return new MockEmailServiceImpl();
    }
}
