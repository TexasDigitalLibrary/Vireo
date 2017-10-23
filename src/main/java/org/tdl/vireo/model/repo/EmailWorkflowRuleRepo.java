package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface EmailWorkflowRuleRepo extends WeaverRepo<EmailWorkflowRule>, EmailWorkflowRuleRepoCustom {

    public EmailWorkflowRule findBySubmissionStatusAndEmailRecipientAndEmailTemplate(SubmissionStatus newSubmissionStatus, EmailRecipient emailRecipient, EmailTemplate newEmailTemplate);

}
