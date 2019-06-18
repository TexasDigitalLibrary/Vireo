package org.tdl.vireo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.weaver.email.service.EmailSender;

@Service
public class SubmissionEmailService {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private TemplateUtility templateUtility;

    /**
     * Manually send the e-mails to the advisors for a given Submission.
     *
     * @param user Associated User.
     * @param submission Associated Submission.
     */
    public void sendAdvisorEmails(User user, Submission submission) {
        InputType contactInputType = inputTypeRepo.findByName("INPUT_CONTACT");
        EmailTemplate template = emailTemplateRepo.findByNameAndSystemRequired("SYSTEM Advisor Review Request", true);

        String subject = templateUtility.compileString(template.getSubject(), submission);
        String content = templateUtility.compileTemplate(template, submission);

        List<String> fullRecipientList = new ArrayList<>();

        // TODO: this needs to only send email to the advisor not any field value that is contact type
        submission.getFieldValuesByInputType(contactInputType).forEach(fv -> {
            SimpleMailMessage smm = new SimpleMailMessage();

            List<String> recipientList = new ArrayList<>();
            for (String contact : fv.getContacts()) {
                if (!fullRecipientList.contains(contact)) {
                    fullRecipientList.add(contact);
                    recipientList.add(contact);
                }
            }

            smm.setTo(String.join(",", recipientList));
            smm.setTo(recipientList.toArray(new String[0]));

            if ("true".equals(user.getSetting("ccEmail"))) {
                String preferedEmail = user.getSetting("preferedEmail");
                smm.setBcc(preferedEmail == null ? user.getEmail() : preferedEmail);
            }

            smm.setSubject(subject);
            smm.setText(content);

            emailSender.send(smm);
        });

        actionLogRepo.createPublicLog(submission, user, "Advisor review email manually generated.");
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
            String subject = (String) data.get("subject");
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
            } else {
                recipientEmails.append(";");
            }

            if (user.getSetting("ccEmail") != null && user.getSetting("ccEmail").equals("true")) {
                String preferredEmail = user.getSetting("preferedEmail");
                smm.setBcc(preferredEmail == null ? user.getEmail() : preferredEmail);
            }

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

        emails.forEach(emailRecipientNode -> {
            String type = (String) emailRecipientNode.get("type");
            EmailRecipient recipient = buildEmailRecipient(type, emailRecipientNode, submission);
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
     * @param type Type of the e-mail.
     * @param emailRecipientMap Recipient e-mail mapping.
     * @param submission Associated Submission.
     *
     * @return A single e-mail recipient.
     */
    private EmailRecipient buildEmailRecipient(String type, HashMap<String, Object> emailRecipientMap, Submission submission) {
      EmailRecipient recipient = null;

      switch(EmailRecipientType.valueOf(type)) {
        case ASSIGNEE: {
          recipient = new EmailRecipientAssignee();
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
      }

      return recipient;
    }

}
