package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.weaver.email.config.WeaverEmailConfig;
import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.email.service.WeaverEmailService;

@Configuration
@Profile(value = { "!test" })
public class AppEmailConfig extends WeaverEmailConfig {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Bean
    @Override
    public EmailSender emailSender() {
        WeaverEmailService emailService = new WeaverEmailService();

        emailService.setDefaultEncoding("UTF-8");

        emailService.setHost(getConfigValue(ConfigurationName.APPLICATION_MAIL_HOST, defaultHost));
        emailService.setFrom(getConfigValue(ConfigurationName.APPLICATION_MAIL_FROM, defaultFrom));
        emailService.setReplyTo(getConfigValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, defaultReplyTo));

        // some hardcoded defaults
        emailService.setPort(getConfigValue(ConfigurationName.APPLICATION_MAIL_PORT, 25));
        emailService.setProtocol(getConfigValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, "smtp"));
        emailService.setUsername(getConfigValue(ConfigurationName.APPLICATION_MAIL_USER, (String) null));
        emailService.setPassword(getConfigValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, (String) null));
        emailService.setChannel(getConfigValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, "clear"));

        return emailService;
    }

    private String getConfigValue(String name, String defaultValue) {
        String value = configurationRepo.getValueByName(name);
        return (value != null) ? value : defaultValue;
    }

    private Integer getConfigValue(String name, Integer defaultValue) {
        Integer value = Integer.getInteger(configurationRepo.getValueByName(name));
        return (value != null) ? value : defaultValue;
    }

}
