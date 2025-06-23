package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface EmailWorkflowRuleRepo extends WeaverRepo<EmailWorkflowRuleByStatus>, EmailWorkflowRuleRepoCustom<EmailWorkflowRuleByStatus, SubmissionStatus> {

    public List<EmailWorkflowRuleByStatus> findByEmailRecipientAndIsDisabled(EmailRecipient emailRecipient, Boolean isDisabled);

    public EmailWorkflowRuleByStatus findBySubmissionStatusAndEmailRecipientAndEmailTemplate(SubmissionStatus newSubmissionStatus, EmailRecipient emailRecipient, EmailTemplate newEmailTemplate);

}
