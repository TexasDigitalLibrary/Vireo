package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.framework.config.CoreEmailConfig;
import edu.tamu.framework.util.CoreEmailUtility;
import edu.tamu.framework.util.EmailSender;

@Configuration
@Profile(value = { "!test" })
public class AppEmailConfig extends CoreEmailConfig {

    @Value("${app.email.host}")
    private String defaultHost;

    @Value("${app.email.from}")
    private String defaultFrom;

    @Value("${app.email.replyTo}")
    private String defaultReplyTo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Override
    @Bean
    public EmailSender emailSender() {
        CoreEmailUtility emailUtility = new CoreEmailUtility();

        emailUtility.setDefaultEncoding("UTF-8");
        
        emailUtility.setFrom(getConfigValue(ConfigurationName.APPLICATION_MAIL_FROM, defaultFrom));
        emailUtility.setReplyTo(getConfigValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, defaultReplyTo));

        emailUtility.setHost(getConfigValue(ConfigurationName.APPLICATION_MAIL_HOST, defaultHost));

        // some hardcoded defaults
        emailUtility.setPort(getConfigValue(ConfigurationName.APPLICATION_MAIL_PORT, 25));
        emailUtility.setProtocol(getConfigValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, "smtp"));
        emailUtility.setUsername(getConfigValue(ConfigurationName.APPLICATION_MAIL_USER, (String) null));
        emailUtility.setPassword(getConfigValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, (String) null));
        emailUtility.setChannel(getConfigValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, "clear"));

        return emailUtility;
    }

    private String getConfigValue(String name, String defaultValue) {
        String value = configurationRepo.getValueByName(name);
        return (value != null) ? value:defaultValue;
    }

    private Integer getConfigValue(String name, Integer defaultValue) {
        Integer value =  Integer.getInteger(configurationRepo.getValueByName(name));
        return (value != null) ? value:defaultValue;
    }
}
