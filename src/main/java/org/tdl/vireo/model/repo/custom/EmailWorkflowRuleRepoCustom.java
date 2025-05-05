package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.SubmissionStatus;

public interface EmailWorkflowRuleRepoCustom {

    public EmailWorkflowRuleByStatus create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate);

    public EmailWorkflowRuleByStatus create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem);

}
