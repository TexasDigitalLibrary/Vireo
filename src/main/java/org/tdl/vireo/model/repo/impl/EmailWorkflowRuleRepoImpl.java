package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

public class EmailWorkflowRuleRepoImpl implements EmailWorkflowRuleRepoCustom {

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Override
    public EmailWorkflowRule create(SubmissionState submissionState, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRule(submissionState, emailRecipient, emailTemplate));
    }

    @Override
    public EmailWorkflowRule create(SubmissionState submissionState, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRule(submissionState, emailRecipient, emailTemplate, isSystem));
    }

}
