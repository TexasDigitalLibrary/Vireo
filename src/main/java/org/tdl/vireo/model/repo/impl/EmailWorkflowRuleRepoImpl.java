package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.RecipientType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

public class EmailWorkflowRuleRepoImpl implements  EmailWorkflowRuleRepoCustom{
	@Autowired
	EmailWorkflowRuleRepo emailWorkflowRuleRepo;

	@Override
	public EmailWorkflowRule create(SubmissionState submissionState,RecipientType recipientType, EmailTemplate emailTemplate) {
		return emailWorkflowRuleRepo.save(new EmailWorkflowRule(submissionState,recipientType,emailTemplate));
	}
}
