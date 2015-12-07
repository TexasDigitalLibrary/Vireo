package org.tdl.vireo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tdl.vireo.service.EmailService;
import org.tdl.vireo.service.EmailServiceImpl;

@Configuration
@Profile(value = { "!test" })
public class MailConfig {

    @Bean
    public EmailService emailService() {
        return new EmailServiceImpl();
    }
}
