package org.tdl.vireo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tdl.vireo.service.VireoEmailSender;

import edu.tamu.weaver.email.config.WeaverEmailConfig;
import edu.tamu.weaver.email.service.EmailSender;

@Configuration
@Profile(value = { "!test" })
public class AppEmailConfig extends WeaverEmailConfig {

    @Bean
    @Override
    public EmailSender emailSender() {
        return new VireoEmailSender();
    }
}
