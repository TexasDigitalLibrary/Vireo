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
 * Dispatch the credential-change notifications.
 *
 * Every notification this service sends is advisory: the credential change it reports has already
 * been persisted by the time the notification is attempted. An absent Email_Template or a mail
 * transport failure is therefore recorded in the application log and swallowed, so the completed
 * change stands.
 *
 * Reset-initiation mail is deliberately not sent from this service. That path reports an ERROR
 * {@code ApiResponse} to the initiating Administrator on a mail failure, which is the opposite
 * failure semantics, so it stays in the controller. Only the nullable template lookup is shared.
 *
 * Notifications are dispatched from the request thread that persisted the change. No dispatch state
 * is retained and no retry is performed.
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
     * Notify the holder of an Account that the stored password on that Account has changed.
     *
     * Never throws. An absent Email_Template or a {@link MessagingException} is recorded in the
     * application log, naming the Account and the notification, and swallowed, so the completed
     * password change stands. Neither the password value nor any token appears in the log or in the
     * message body.
     *
     * @param user The Account whose password changed.
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
     * Resolve an Email_Template by name, preferring a custom row over the system row.
     *
     * The result of {@code findByNameOverride} is tested for null before any {@code getSubject()}
     * or {@code getMessage()} call, so a deployment where the row was never installed produces a
     * logged skip rather than a {@link NullPointerException}.
     *
     * Shared with the reset-initiation path, which reports ERROR on a null result while the
     * notifications log and continue.
     *
     * @param name The Email_Template name.
     *
     * @return The resolved template, or null when neither a custom nor a system row exists.
     */
    public EmailTemplate resolveTemplate(String name) {
        EmailTemplate emailTemplate = emailTemplateRepo.findByNameOverride(name);

        if (emailTemplate == null) {
            LOG.warn("No email template found named '" + name + "'.");

            return null;
        }

        return emailTemplate;
    }

    /**
     * Format the time of the change for the {@code CHANGE_TIME} template parameter.
     *
     * The value is rendered at {@link ZoneOffset#UTC} and labelled as such, so the timestamp is
     * unambiguous for a recipient in any timezone.
     *
     * @return The formatted time of the change.
     */
    private String formatChangeTime() {
        return CHANGE_TIME_FORMAT.format(clock.instant());
    }

}
