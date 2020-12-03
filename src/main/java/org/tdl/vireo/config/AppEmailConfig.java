package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${app.email.username:''}")
    private String username;
    
    @Value("${app.email.password:''}")
    private String password;
    
    @Value("${app.email.port:25}")
    private int port;
    
    @Value("${app.email.protocol:smtp}")
    private String protocol;
    
    @Value("${app.email.channel:clear}")
    private String channel;

    @Bean
    @Override
    public EmailSender emailSender() {
        WeaverEmailService emailService = new WeaverEmailService();

        emailService.setDefaultEncoding("UTF-8");

        emailService.setHost(getConfigValue(ConfigurationName.APPLICATION_MAIL_HOST, defaultHost));
        emailService.setFrom(getConfigValue(ConfigurationName.APPLICATION_MAIL_FROM, defaultFrom));
        emailService.setReplyTo(getConfigValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, defaultReplyTo));

        // some hardcoded defaults
        emailService.setPort(getConfigValue(ConfigurationName.APPLICATION_MAIL_PORT, port));
        emailService.setProtocol(getConfigValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, protocol));
        emailService.setUsername(getConfigValue(ConfigurationName.APPLICATION_MAIL_USER, (String) username));
        emailService.setPassword(getConfigValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, (String) password));
        emailService.setChannel(getConfigValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, channel));

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
