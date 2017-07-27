package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SubmissionStatus;

public interface EmailWorkflowRuleRepoCustom {

    public EmailWorkflowRule create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate);

    public EmailWorkflowRule create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem);

}
