package org.tdl.vireo.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.VireoEmailConfig;

import edu.tamu.weaver.email.service.EmailSender;

/**
 * Provide e-mail sending specific to the Submission process.
 *
 * E-mails are built utilizing MimeMessageHelper.
 *
 * @see EmailSender
 * @see MimeMessageHelper
 */
@Service
public class VireoEmailSender extends JavaMailSenderImpl implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(VireoEmailSender.class);

    @Autowired
    private VireoEmailConfig vireoEmailConfig;

    public void loadConfiguration() {
        setDefaultEncoding(vireoEmailConfig.getEncoding());
        setHost(vireoEmailConfig.getHost());
        setPort(vireoEmailConfig.getPort());
        setProtocol(vireoEmailConfig.getProtocol());

        Properties properties = super.getJavaMailProperties();

        if (vireoEmailConfig.getUsername() != null && vireoEmailConfig.getPassword() != null) {
            setUsername(vireoEmailConfig.getUsername());
            setPassword(vireoEmailConfig.getPassword());

            properties.setProperty("mail.smtp.auth", "true");
        } else {
            properties.setProperty("mail.smtp.auth", "false");
        }

        if (vireoEmailConfig.getChannel().equals("starttls")) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtps.ssl.checkserveridentity", "false");
        } else if (vireoEmailConfig.getChannel().equals("ssl")) {
            properties.setProperty("mail.smtps.ssl.checkserveridentity", "true");
            properties.setProperty("mail.smtp.starttls.enable", "false");
        }

        setJavaMailProperties(properties);
    }

    public void sendEmail(String to, String[] bcc, String subject, String content) throws MessagingException {
        sendEmail(new String[] { to }, new String[] { }, bcc, subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        sendEmail(new String[] { to }, new String[] { }, new String[] { }, subject, content);
    }

    public void sendEmail(String[] to, String subject, String content) throws MessagingException {
        sendEmail(to, new String[] { }, new String[] { }, subject, content);
    }

    public void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String content) throws MessagingException {
        loadConfiguration();

        MimeMessage message = createMimeMessage();
        MimeMessageHelper mm = new MimeMessageHelper(message);

        mm.setFrom(vireoEmailConfig.getFrom());
        mm.setReplyTo(vireoEmailConfig.getReplyTo());

        mm.setTo(to);
        mm.setCc(cc);
        mm.setBcc(bcc);
        mm.setSubject(subject);
        mm.setText(content, false);

        LOG.debug("\tSending email with subject '" + subject + "' from " + vireoEmailConfig.getFrom() + " to: [ " + String.join(";", to) + " ]; ");
        send(message);
    }

}
