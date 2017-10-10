package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

public class EmailWorkflowRuleRepoImpl implements EmailWorkflowRuleRepoCustom {

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Override
    public EmailWorkflowRule create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRule(submissionStatus, emailRecipient, emailTemplate));
    }

    @Override
    public EmailWorkflowRule create(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRule(submissionStatus, emailRecipient, emailTemplate, isSystem));
    }

}
