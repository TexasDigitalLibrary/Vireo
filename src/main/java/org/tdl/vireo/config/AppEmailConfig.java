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
        emailUtility.setFrom(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_FROM, defaultFrom));
        emailUtility.setReplyTo(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, defaultReplyTo));
    	emailUtility.setHost(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_HOST, defaultHost));
    	// some hardcoded defaults
    	emailUtility.setPort(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PORT, 25));
    	emailUtility.setProtocol(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, "smtp"));
    	emailUtility.setUsername(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_USER, (String) null));
    	emailUtility.setPassword(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, (String) null));
    	emailUtility.setChannel(configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, "clear"));        
        return emailUtility;
    }
	 
}
