package org.tdl.vireo.service;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.utility.TemplateUtility;

/**
 * Sends credential-change notifications.
 *
 * Notifications are advisory: the change has already been persisted, so a missing template or mail
 * failure is logged and swallowed. Reset-initiation mail is not sent here because it must report
 * mail failures to the administrator; only the template lookup is shared.
 */
@Service
public class CredentialNotificationService {

    public static final String PASSWORD_RESET_REQUEST_TEMPLATE = "SYSTEM Password Reset Request";

    public static final String PASSWORD_CHANGED_TEMPLATE = "SYSTEM Password Changed";

    public static final String EMAIL_CHANGED_TEMPLATE = "SYSTEM Email Address Changed";

    private static final String PASSWORD_CHANGED_NOTIFICATION = "password-changed notification";

    private static final String CHANGE_TIME_PARAMETER = "CHANGE_TIME";

    private static final DateTimeFormatter CHANGE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withZone(ZoneOffset.UTC);

    private static final Logger LOG = LoggerFactory.getLogger(CredentialNotificationService.class);

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private VireoEmailSender emailSender;

    private Clock clock = Clock.systemUTC();

    /**
     * Notifies the holder of an Account that its password changed. Never throws; no password or
     * token appears in the log or message body.
     */
    public void notifyPasswordChanged(User user) {
        if (user == null) {
            LOG.warn("No account supplied, skipping the " + PASSWORD_CHANGED_NOTIFICATION + ".");

            return;
        }

        String recipient = user.getEmail();

        if (recipient == null || recipient.trim().isEmpty()) {
            LOG.warn("No email address on account '" + user.getId() + "', skipping the " + PASSWORD_CHANGED_NOTIFICATION + ".");

            return;
        }

        EmailTemplate emailTemplate = resolveTemplate(PASSWORD_CHANGED_TEMPLATE);

        if (emailTemplate == null) {
            LOG.warn("Skipping the " + PASSWORD_CHANGED_NOTIFICATION + " for account '" + recipient + "', because the email template '" + PASSWORD_CHANGED_TEMPLATE + "' is not installed.");

            return;
        }

        String content = templateUtility.templateParameters(emailTemplate.getMessage(), new String[][] { { CHANGE_TIME_PARAMETER, formatChangeTime() } });

        try {
            emailSender.sendEmail(recipient, emailTemplate.getSubject(), content);
        } catch (MessagingException e) {
            LOG.warn("Unable to send the " + PASSWORD_CHANGED_NOTIFICATION + " to account '" + recipient + "', because: " + e.getMessage());
        }
    }

    /**
     * Resolves an email template by name, preferring a custom row over the system row.
     *
     * @return The template, or null (logged) when neither exists.
     */
    public EmailTemplate resolveTemplate(String name) {
        EmailTemplate emailTemplate = emailTemplateRepo.findByNameOverride(name);

        if (emailTemplate == null) {
            LOG.warn("No email template found named '" + name + "'.");

            return null;
        }

        return emailTemplate;
    }

    /** Formats the current time in UTC, labelled as such, for the {@code CHANGE_TIME} parameter. */
    private String formatChangeTime() {
        return CHANGE_TIME_FORMAT.format(clock.instant());
    }

}
