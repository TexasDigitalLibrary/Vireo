package org.tdl.vireo.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

/**
 * The same as {@link JavaMailSenderImpl} but with a custom constructor and helper method(s)
 * 
 * @author gad
 */
@Service
public class EmailServiceImpl extends JavaMailSenderImpl implements EmailService {
    
    @Autowired
    private ConfigurationRepo configurationRepo;

    public EmailServiceImpl() {
        
    }
    
    @Override
    public void init() {
        String host = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_HOST, "localhost");
        int port = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PORT, 25);
        String protocol = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, "smtp"); // smtp or smtps
        String username = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_USER, (String) null);
        String password = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, (String) null);
        String channel = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, "clear"); // clear, starttls, or ssl

        // set the mail hostname from DB, or default to "localhost"
        setHost(host);
        // set the mail port from DB, or default to 25
        setPort(port);
        // set the mail protocol from DB, or default to "smtp"
        setProtocol(protocol);

        // set the rest of the properties
        Properties javaMailProperties = new Properties();
        switch (channel) {
        case "starttls":
            // set ssl from DB, or default to off
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
            break;
        case "ssl":
            // default to enforce ssl validity
            javaMailProperties.setProperty("mail.smtps.ssl.checkserveridentity", "true");
            // default to force ssl to trust all certs -- only needed for self-signed certs
            // javaMailProperties.setProperty("mail.smtps.ssl.trust", "*");
            break;
        default:
        }
        if (username != null && password != null) {
            // set the mail username from DB, or default to null
            setUsername(username);
            // set the mail password from DB, or default to null
            setPassword(password);
            // set authentication depending on if we have a username and password set
            javaMailProperties.setProperty("mail.smtp.auth", "true");
        }
        setJavaMailProperties(javaMailProperties);
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        String from = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_FROM, "noreply@tdl.org");
        String replyTo = configurationRepo.getValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, "dev@tdl.org");

        MimeMessage message = createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from);
        helper.setReplyTo(replyTo);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        send(message);
    }
}
