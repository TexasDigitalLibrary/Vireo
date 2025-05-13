package org.tdl.vireo.service;

import javax.mail.MessagingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientAssignee;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientOrganization;
import org.tdl.vireo.model.EmailRecipientPlainAddress;
import org.tdl.vireo.model.EmailRecipientSubmitter;
import org.tdl.vireo.model.EmailRecipientType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleByActionRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.impl.AbstractEmailRecipientRepoImpl;
import org.tdl.vireo.utility.TemplateUtility;

/**
 * Provide e-mail sending specific to the Submission process.
 *
 * E-mails are built utilizing SimpleMailMessage and then sent via EmailSender.
 *
 * @see VireoEmailSender
 * @see SimpleMailMessage
 */
@Service
public class SubmissionEmailService {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AbstractEmailRecipientRepoImpl abstractEmailRecipientRepoImpl;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private EmailWorkflowRuleByActionRepo emailWorkflowRuleByActionRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private VireoEmailSender emailSender;

    /**
     * Manually send the e-mails to the advisors for a given Submission.
     *
     * @param user Associated User.
     */
    public void sendAdvisorEmails(User user, Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId).get();

        EmailRecipient advisorRecipient = abstractEmailRecipientRepoImpl.createAdvisorRecipient();
        List<EmailWorkflowRuleByStatus> emailWorkflowRules = emailWorkflowRuleRepo.findByEmailRecipientAndIsDisabled(advisorRecipient, false);

        if (emailWorkflowRules.size() == 0) {
            actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "No Advisor email workflow rules are defined and enabled.");
        }
        else {
            boolean emailed = false;
            Iterator<EmailWorkflowRuleByStatus> emailWorkflowRuleIterator = emailWorkflowRules.iterator();

            while (emailWorkflowRuleIterator.hasNext()) {
                EmailWorkflowRuleByStatus emailWorkflowRule = emailWorkflowRuleIterator.next();
                EmailTemplate template = emailWorkflowRule.getEmailTemplate();
                String subject = templateUtility.compileString(template.getSubject(), submission);
                String content = templateUtility.compileTemplate(template, submission);

                List<FieldValue> advisorList = submission.getFieldValuesByPredicateValue("dc.contributor.advisor");

                List<String> recipientList = new ArrayList<>();
                advisorList.forEach(afv -> {
                  for (String afvcontact : afv.getContacts()) {
                      if (!recipientList.contains(afvcontact)) {
                          recipientList.add(afvcontact);
                      }
                  }
                });

                if (!recipientList.isEmpty()) {
                    try {
                        String[] to = recipientList.toArray(new String[0]);
                        emailSender.sendEmail(to, subject, content);

                        emailed = true;
                    } catch (MessagingException me) {
                        LOG.error("Problem sending email: " + me.getMessage());
                    }
                }
            }

            if (emailed) {
                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Advisor review email manually generated.");
            } else {
                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "No Advisor review emails to generate.");
            }
        }
    }

    /**
     * Send an e-mail associated with the given user and submission.
     *
     * @param user Associated User.
     * @param submissionId ID of the associated Submission.
     * @param data Mapping of data.
     *
     * @throws JsonProcessingException
     * @throws IOException
     */
    public void sendAutomatedEmails(User user, Long submissionId, Map<String, Object> data) throws JsonProcessingException, IOException {
        Submission submission = submissionRepo.findById(submissionId).get();

        if (data.containsKey("sendEmailToRecipient") && (boolean) data.get("sendEmailToRecipient")) {
            String subject = templateUtility.compileString((String) data.get("subject"), submission);
            String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
            StringBuilder recipientEmails = new StringBuilder();

            List<String> recipientEmailAddresses = buildEmailRecipients("recipientEmails", submission, data);
            List<String> recipientCcEmailAddresses = new ArrayList<>();

            if (data.containsKey("sendEmailToCCRecipient") && (boolean) data.get("sendEmailToCCRecipient")) {
                recipientCcEmailAddresses = buildEmailRecipients("ccRecipientEmails", submission, data);
            }

            try {
                String[] to = recipientEmailAddresses.toArray(new String[0]);
                String[] cc = recipientCcEmailAddresses.toArray(new String[0]);
                String[] bcc = new String[] { };

                recipientEmails.append("Email sent to: [ " + String.join(";", recipientEmailAddresses) + " ]; ");

                if (data.containsKey("sendEmailToCCRecipient") && (boolean) data.get("sendEmailToCCRecipient")) {
                    List<String> ccRecipientEmailAddresses = buildEmailRecipients("ccRecipientEmails", submission, data);
                    cc = ccRecipientEmailAddresses.toArray(new String[0]);
                    recipientEmails.append(" and cc to: [ " + String.join(";", ccRecipientEmailAddresses) + " ]; ");
                }

                if (user.getSetting("ccEmail") != null && user.getSetting("ccEmail").equals("true")) {
                    String preferredEmail = user.getSetting("preferedEmail");
                    bcc = new String[] { preferredEmail == null ? user.getEmail() : preferredEmail } ;
                }

                emailSender.sendEmail(to, cc, bcc, subject, templatedMessage);

                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, recipientEmails.toString() + subject + ": " + templatedMessage);
            } catch (MessagingException me) {
                LOG.error("Problem sending email: " + me.getMessage());
            }
        }
    }

    /**
     * Process workflow and send e-mails as needed for the given submission.
     *
     * @param user Associated User.
     * @param submissionId The ID of the submission.
     */
    public void sendWorkflowEmails(User user, Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId).get();

        List<EmailWorkflowRuleByStatus> rules = submission.getOrganization().getAggregateEmailWorkflowRules();
        Map<Long, List<String>> recipientLists = new HashMap<>();

        for (EmailWorkflowRuleByStatus rule : rules) {
            if (rule.getSubmissionStatus().equals(submission.getSubmissionStatus()) && !rule.isDisabled()) {
                LOG.debug("Email Workflow Rule " + rule.getId() + " firing for submission " + submission.getId());
                Long templateId = rule.getEmailTemplate().getId();

                if (!recipientLists.containsKey(templateId)) {
                    recipientLists.put(templateId, new ArrayList<>());
                }

                // TODO: Not all variables are currently being replaced.
                String subject = templateUtility.compileString(rule.getEmailTemplate().getSubject(), submission);
                String content = templateUtility.compileTemplate(rule.getEmailTemplate(), submission);

                for (String email : rule.getEmailRecipient().getEmails(submission)) {
                    if (recipientLists.get(templateId).contains(email)) {
                        LOG.debug("\tSkipping (already sent) email to recipient at address " + email);
                        continue;
                    }

                    recipientLists.get(templateId).add(email);

                    try {
                        if ("true".equals(user.getSetting("ccEmail"))) {
                            String preferedEmail = user.getSetting("preferedEmail");
                            String bcc = preferedEmail == null ? user.getEmail() : preferedEmail;
                            emailSender.sendEmail(email, new String[] { bcc }, subject, content);
                        } else {
                            emailSender.sendEmail(email, subject, content);
                        }
                    } catch (MessagingException me) {
                        LOG.error("Problem sending email: " + me.getMessage());
                        recipientLists.get(templateId).remove(email);
                    }
                }
            } else {
                LOG.debug("\tRule disabled or of irrelevant status condition.");
            }
        }
    }

    /**
     * Send any emails for given submission and action log.
     *
     * Check recursively the submission organization for email workflow rules by action.
     *
     * @param submission Submission.
     * @param actionLog ActionLog.
     */
    public void sendActionEmails(Submission submission, ActionLog actionLog) {
        Action action = actionLog.getAction();
        
        switch (action) {
        case STUDENT_MESSAGE:
        case ADVISOR_MESSAGE:
        case ADVISOR_APPROVE_SUBMISSION:
        case ADVISOR_CLEAR_APPROVE_SUBMISSION:
        case ADVISOR_APPROVE_EMBARGO:
        case ADVISOR_CLEAR_APPROVE_EMBARGO:
            processSubmissionActionLog(submission, actionLog);
            LOG.info("Submission {}: {} action logged processed", submission.getId(), action);
            break;
        case UNDETERMINED:
        default:
            break;
        }
        
    }

    private void processSubmissionActionLog(Submission submission, ActionLog actionLog) {
        List<EmailWorkflowRuleByAction> rules = submission.getOrganization().getAggregateEmailWorkflowRulesByAction();
        Map<Long, List<String>> recipientLists = new HashMap<>();

        for (EmailWorkflowRuleByAction rule : rules) {
            if (rule.getAction().equals(actionLog.getAction()) && !rule.isDisabled()) {
                LOG.info("Action Email Workflow Rule " + rule.getId() + " firing for submission " + submission.getId());
                Long templateId = rule.getEmailTemplate().getId();

                if (!recipientLists.containsKey(templateId)) {
                    recipientLists.put(templateId, new ArrayList<>());
                }

                // TODO: Not all variables are currently being replaced.
                String subject = templateUtility.compileString(rule.getEmailTemplate().getSubject(), submission);
                String content = templateUtility.compileTemplate(rule.getEmailTemplate(), submission);

                for (String email : rule.getEmailRecipient().getEmails(submission)) {
                    if (recipientLists.get(templateId).contains(email)) {
                        LOG.debug("\tSkipping (already sent) email to recipient at address " + email);
                        continue;
                    }

                    recipientLists.get(templateId).add(email);

                    try {
                        emailSender.sendEmail(email, subject, content);
                    } catch (MessagingException me) {
                        LOG.error("Problem sending email: " + me.getMessage());
                        recipientLists.get(templateId).remove(email);
                    }
                }
            } else {
                LOG.debug("\tRule disabled or of irrelevant status condition.");
            }
        }
    }

    /**
     * Build the e-mail recipient list.
     *
     * @param property Property name to extract from the data map.
     * @param submission Associated Submission.
     * @param data Mapping of data.
     *
     * @return A distinct array of e-mails.
     */
    @SuppressWarnings("unchecked")
    private List<String> buildEmailRecipients(String property, Submission submission, Map<String, Object> data) {
        List<String> recipients = new ArrayList<>();
        List<HashMap<String, Object>> emails = (List<HashMap<String, Object>>) data.get(property);
        emails.forEach(emailRecipientMap -> {
            EmailRecipient recipient = buildEmailRecipient(emailRecipientMap, submission);
            if (recipient != null) {
                for (String recipientEmail : recipient.getEmails(submission)) {
                    if (!recipients.contains(recipientEmail)) {
                        recipients.add(recipientEmail);
                    }
                }
            }
        });

        return recipients;
    }

    /**
     * Build the e-mail recipient.
     *
     * @param emailRecipientMap Recipient e-mail mapping.
     * @param submission Associated Submission.
     *
     * @return A single e-mail recipient.
     */
    private EmailRecipient buildEmailRecipient(HashMap<String, Object> emailRecipientMap, Submission submission) {
      String type = (String) emailRecipientMap.get("type");
      EmailRecipient recipient = null;

      switch(EmailRecipientType.valueOf(type)) {
        case ASSIGNEE: {
          recipient = new EmailRecipientAssignee();
          break;
        }
        case ADVISOR: {
            String label = (String) emailRecipientMap.get("name");
            FieldPredicate fp = fieldPredicateRepo.findByValue("dc.contributor.advisor");
            if (label != null && fp != null) {
              recipient = new EmailRecipientContact(label, fp);
            }
          break;
        }
        case CONTACT: {
          String label = (String) emailRecipientMap.get("name");

          Optional<FieldPredicate> fp = fieldPredicateRepo.findById(Long.valueOf((int) emailRecipientMap.get("data")));
          if (label != null && fp.isPresent()) {
            recipient = new EmailRecipientContact(label, fp.get());
          }
          break;
        }
        case PLAIN_ADDRESS: {
          recipient = new EmailRecipientPlainAddress((String) emailRecipientMap.get("name"));
          break;
        }
        case ORGANIZATION: {
          recipient = new EmailRecipientOrganization(submission.getOrganization());
          break;
        }
        case SUBMITTER: {
          recipient = new EmailRecipientSubmitter();
          break;
        }
        default:
      }

      return recipient;
    }

}
