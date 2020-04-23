package org.tdl.vireo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientAssignee;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientOrganization;
import org.tdl.vireo.model.EmailRecipientPlainAddress;
import org.tdl.vireo.model.EmailRecipientSubmitter;
import org.tdl.vireo.model.EmailRecipientType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.impl.AbstractEmailRecipientRepoImpl;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.weaver.email.service.WeaverEmailService;

/**
 * Provide e-mail sending specific to the Submission process.
 *
 * E-mails are built utilizing SimpleMailMessage and then sent via EmailSender.
 *
 * @see EmailSender
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
    private WeaverEmailService emailSender;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private TemplateUtility templateUtility;

    /**
     * Manually send the e-mails to the advisors for a given Submission.
     *
     * @param user Associated User.
     * @param submission Associated Submission.
     */
    public void sendAdvisorEmails(User user, Submission submission) {
        EmailRecipient advisorRecipient = abstractEmailRecipientRepoImpl.createAdvisorRecipient();
        List<EmailWorkflowRule> emailWorkflowRules = emailWorkflowRuleRepo.findByEmailRecipientAndIsDisabled(advisorRecipient, false);

        if (emailWorkflowRules.size() == 0) {
            actionLogRepo.createPublicLog(submission, user, "No Advisor email workflow rules are defined and enabled.");
        }
        else {
            boolean emailed = false;
            Iterator<EmailWorkflowRule> emailWorkflowRuleIterator = emailWorkflowRules.iterator();

            while (emailWorkflowRuleIterator.hasNext()) {
                EmailWorkflowRule emailWorkflowRule = emailWorkflowRuleIterator.next();
                EmailTemplate template = emailWorkflowRule.getEmailTemplate();
                String subject = templateUtility.compileString(template.getSubject(), submission);
                String content = templateUtility.compileTemplate(template, submission);

                List<FieldValue> advisorList = submission.getFieldValuesByPredicateValue("dc.contributor.advisor");
                SimpleMailMessage smm = new SimpleMailMessage();
                List<String> recipientList = new ArrayList<>();
                advisorList.forEach(afv -> {
                  for (String afvcontact : afv.getContacts()) {
                      if (!recipientList.contains(afvcontact)) {
                          recipientList.add(afvcontact);
                      }
                  }
                });

                if (!recipientList.isEmpty()) {
                    //FROM email address not utilized by WeaverEmailService unless explicitly set in SimpleMailMessage - likely needs a fix in WeaverEmailService.java
                    smm.setFrom(emailSender.getFrom());
                    smm.setTo(recipientList.toArray(new String[0]));
                    smm.setSubject(subject);
                    smm.setText(content);

                    emailSender.send(smm);
                    emailed = true;
                }
            }

            if (emailed) {
                actionLogRepo.createPublicLog(submission, user, "Advisor review email manually generated.");
            } else {
                actionLogRepo.createPublicLog(submission, user, "No Advisor review emails to generate.");
            }
        }
    }

    /**
     * Send an e-mail associated with the given user and submission.
     *
     * @param user Associated User.
     * @param submission Associated Submission.
     * @param data Mapping of data.
     *
     * @throws JsonProcessingException
     * @throws IOException
     */
    public void sendAutomatedEmails(User user, Submission submission, Map<String, Object> data) throws JsonProcessingException, IOException {
        if (data.containsKey("sendEmailToRecipient") && (boolean) data.get("sendEmailToRecipient")) {
            String subject = templateUtility.compileString((String) data.get("subject"), submission);
            String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
            StringBuilder recipientEmails = new StringBuilder();
            SimpleMailMessage smm = new SimpleMailMessage();

            List<String> recipientEmailAddresses = buildEmailRecipients("recipientEmails", submission, data);
            smm.setTo(recipientEmailAddresses.toArray(new String[0]));

            recipientEmails.append("Email sent to: [ " + String.join(";", recipientEmailAddresses) + " ]; ");

            if (data.containsKey("sendEmailToCCRecipient") && (boolean) data.get("sendEmailToCCRecipient")) {
                List<String> ccRecipientEmailAddresses = buildEmailRecipients("ccRecipientEmails", submission, data);
                smm.setCc(ccRecipientEmailAddresses.toArray(new String[0]));
                recipientEmails.append(" and cc to: [ " + String.join(";", ccRecipientEmailAddresses) + " ]; ");
            }

            if (user.getSetting("ccEmail") != null && user.getSetting("ccEmail").equals("true")) {
                String preferredEmail = user.getSetting("preferedEmail");
                smm.setBcc(preferredEmail == null ? user.getEmail() : preferredEmail);
            }

            smm.setFrom(emailSender.getFrom());
            smm.setSubject(subject);
            smm.setText(templatedMessage);

            emailSender.send(smm);

            actionLogRepo.createPublicLog(submission, user, recipientEmails.toString() + subject + ": " + templatedMessage);
        }
    }

    /**
     * Process workflow and send e-mails as needed for the given submission.
     *
     * @param user Associated User.
     * @param submission Associated Submission.
     */
    public void sendWorkflowEmails(User user, Submission submission) {
        SimpleMailMessage smm = new SimpleMailMessage();

        List<EmailWorkflowRule> rules = submission.getOrganization().getAggregateEmailWorkflowRules();
        Map<Long, List<String>> recipientLists = new HashMap<>();

        for (EmailWorkflowRule rule : rules) {
            LOG.debug("Email Workflow Rule " + rule.getId() + " firing for submission " + submission.getId());

            if (rule.getSubmissionStatus().equals(submission.getSubmissionStatus()) && !rule.isDisabled()) {
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
                        LOG.debug("\tSending email to recipient at address " + email);

                        smm.setTo(email);

                        if ("true".equals(user.getSetting("ccEmail"))) {
                            String preferedEmail = user.getSetting("preferedEmail");
                            smm.setBcc(preferedEmail == null ? user.getEmail() : preferedEmail);
                        }

                        smm.setFrom(emailSender.getFrom());
                        smm.setSubject(subject);
                        smm.setText(content);

                        emailSender.send(smm);
                    } catch (MailException me) {
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
            if (label != null & fp != null) {
              recipient = new EmailRecipientContact(label, fp);
            }
          break;
        }
        case CONTACT: {
          String label = (String) emailRecipientMap.get("name");
          FieldPredicate fp = fieldPredicateRepo.getOne(new Long((Integer)emailRecipientMap.get("data")));
          if(label != null & fp != null) {
            recipient = new EmailRecipientContact(label, fp);
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
